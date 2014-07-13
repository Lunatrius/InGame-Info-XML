package com.github.lunatrius.ingameinfo.handler;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.lib.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigurationHandler {
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_ALIGNMENT = "alignment";

	public static final String FILENAME = "filename";
	public static final String FILENAME_DESC = "The configuration that that should be loaded on startup.";

	public static final String SHOWINCHAT = "showInChat";
	public static final String SHOWINCHAT_DESC = "Display the overlay in chat.";

	public static final String SHOWONPLAYERLIST = "showOnPlayerList";
	public static final String SHOWONPLAYERLIST_DESC = "Display the overlay on the player list.";

	public static final String ALIGNMENT_DESC = "Offsets for %s (X<space>Y).";
	public static final String LANG_PREFIX = Reference.MODID.toLowerCase();

	public static Configuration configuration;

	public static String configName = "InGameInfo.xml";
	public static boolean showInChat = true;
	public static boolean showOnPlayerList = true;

	private static Property propConfigName = null;
	private static Property propShowInChat = null;
	private static Property propShowOnPlayerList = null;
	private static final Map<Alignment, Property> propAlignments = new HashMap<Alignment, Property>();

	public static void init(File configFile) {
		if (configuration == null) {
			configuration = new Configuration(configFile);
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {
		propConfigName = configuration.get(CATEGORY_GENERAL, FILENAME, "InGameInfo.xml", FILENAME_DESC);
		propConfigName.setLanguageKey(String.format("%s.%s", LANG_PREFIX, FILENAME));
		propConfigName.setRequiresMcRestart(true);
		configName = propConfigName.getString();

		propShowInChat = configuration.get(CATEGORY_GENERAL, SHOWINCHAT, showInChat, SHOWINCHAT_DESC);
		propShowInChat.setLanguageKey(String.format("%s.%s", LANG_PREFIX, SHOWINCHAT));
		showInChat = propShowInChat.getBoolean(showInChat);

		propShowOnPlayerList = configuration.get(CATEGORY_GENERAL, SHOWONPLAYERLIST, showOnPlayerList, SHOWONPLAYERLIST_DESC);
		propShowOnPlayerList.setLanguageKey(String.format("%s.%s", LANG_PREFIX, SHOWONPLAYERLIST));
		showOnPlayerList = propShowOnPlayerList.getBoolean(showOnPlayerList);

		for (Alignment alignment : Alignment.values()) {
			Property property = configuration.get(CATEGORY_ALIGNMENT, alignment.toString().toLowerCase(), alignment.getXY(), String.format(ALIGNMENT_DESC, alignment.toString()));
			property.setLanguageKey(String.format("%s.%s", LANG_PREFIX, alignment.toString().toLowerCase()));
			property.setValidationPattern(Pattern.compile("-?\\d+ -?\\d+"));
			System.out.println(property.getLanguageKey());
			propAlignments.put(alignment, property);
			alignment.setXY(property.getString());
		}

		save();
	}

	public static void reload() {
		loadConfiguration();
		save();
	}

	public static void save() {
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	public static void setConfigName(String name) {
		propConfigName.set(name);
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase(Reference.MODID)) {
			loadConfiguration();
		}
	}
}
