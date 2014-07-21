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

	public static final String REPLACEDEBUG = "replaceDebug";
	public static final String REPLACEDEBUG_DESC = "Replace the debug overlay (F3) with the InGameInfoXML overlay.";

	public static final String SHOWINCHAT = "showInChat";
	public static final String SHOWINCHAT_DESC = "Display the overlay in chat.";

	public static final String SHOWONPLAYERLIST = "showOnPlayerList";
	public static final String SHOWONPLAYERLIST_DESC = "Display the overlay on the player list.";

	public static final String ALIGNMENT_DESC = "Offsets for %s (X<space>Y).";
	public static final String LANG_PREFIX = Reference.MODID.toLowerCase() + ".config";

	public static Configuration configuration;

	public static final String CONFIGNAME_DEFAULT = "InGameInfo.xml";
	// TODO: 1.8 - flip the default to true
	public static final boolean REPLACEDEBUG_DEFAULT = false;
	public static final boolean SHOWINCHAT_DEFAULT = true;
	public static final boolean SHOWONPLAYERLIST_DEFAULT = true;

	public static String configName = CONFIGNAME_DEFAULT;
	public static boolean replaceDebug = REPLACEDEBUG_DEFAULT;
	public static boolean showInChat = SHOWINCHAT_DEFAULT;
	public static boolean showOnPlayerList = SHOWONPLAYERLIST_DEFAULT;

	private static Property propConfigName = null;
	private static Property propReplaceDebug = null;
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
		propConfigName = configuration.get(CATEGORY_GENERAL, FILENAME, CONFIGNAME_DEFAULT, FILENAME_DESC);
		propConfigName.setLanguageKey(String.format("%s.%s", LANG_PREFIX, FILENAME));
		propConfigName.setRequiresMcRestart(true);
		configName = propConfigName.getString();

		propReplaceDebug = configuration.get(CATEGORY_GENERAL, REPLACEDEBUG, REPLACEDEBUG_DEFAULT, REPLACEDEBUG_DESC);
		propReplaceDebug.setLanguageKey(String.format("%s.%s", LANG_PREFIX, REPLACEDEBUG));
		replaceDebug = propReplaceDebug.getBoolean(REPLACEDEBUG_DEFAULT);

		propShowInChat = configuration.get(CATEGORY_GENERAL, SHOWINCHAT, SHOWINCHAT_DEFAULT, SHOWINCHAT_DESC);
		propShowInChat.setLanguageKey(String.format("%s.%s", LANG_PREFIX, SHOWINCHAT));
		showInChat = propShowInChat.getBoolean(SHOWINCHAT_DEFAULT);

		propShowOnPlayerList = configuration.get(CATEGORY_GENERAL, SHOWONPLAYERLIST, SHOWONPLAYERLIST_DEFAULT, SHOWONPLAYERLIST_DESC);
		propShowOnPlayerList.setLanguageKey(String.format("%s.%s", LANG_PREFIX, SHOWONPLAYERLIST));
		showOnPlayerList = propShowOnPlayerList.getBoolean(SHOWONPLAYERLIST_DEFAULT);

		for (Alignment alignment : Alignment.values()) {
			Property property = configuration.get(CATEGORY_ALIGNMENT, alignment.toString().toLowerCase(), alignment.getXY(), String.format(ALIGNMENT_DESC, alignment.toString()));
			property.setLanguageKey(String.format("%s.%s", LANG_PREFIX, alignment.toString().toLowerCase()));
			property.setValidationPattern(Pattern.compile("-?\\d+ -?\\d+"));
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
