package com.github.lunatrius.ingameinfo.handler;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.reference.Names;
import com.github.lunatrius.ingameinfo.reference.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigurationHandler {
    public static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    public static Configuration configuration;

    public static final String CONFIG_NAME_DEFAULT = Names.Files.FILE_XML;
    public static final boolean REPLACE_DEBUG_DEFAULT = false;
    public static final boolean SHOW_IN_CHAT_DEFAULT = true;
    public static final boolean SHOW_ON_PLAYER_LIST_DEFAULT = true;
    public static final double SCALE_DEFAULT = 1.0;
    public static final int FILE_INTERVAL_DEFAULT = 5;
    public static final boolean SHOW_OVERLAY_POTIONS_DEFAULT = true;
    public static final boolean SHOW_OVERLAY_ITEM_ICONS_DEFAULT = true;

    public static String configName = CONFIG_NAME_DEFAULT;
    public static boolean replaceDebug = REPLACE_DEBUG_DEFAULT;
    public static boolean showInChat = SHOW_IN_CHAT_DEFAULT;
    public static boolean showOnPlayerList = SHOW_ON_PLAYER_LIST_DEFAULT;
    public static float scale = (float) SCALE_DEFAULT;
    public static int fileInterval = FILE_INTERVAL_DEFAULT;
    public static boolean showOverlayPotions = SHOW_OVERLAY_POTIONS_DEFAULT;
    public static boolean showOverlayItemIcons = SHOW_OVERLAY_ITEM_ICONS_DEFAULT;

    public static Property propConfigName = null;
    public static Property propReplaceDebug = null;
    public static Property propShowInChat = null;
    public static Property propShowOnPlayerList = null;
    public static Property propScale = null;
    public static Property propFileInterval = null;
    public static Property propShowOverlayPotions = null;
    public static Property propShowOverlayItemIcons= null;
    public static final Map<Alignment, Property> propAlignments = new HashMap<>();

    private ConfigurationHandler() {}

    public static void init(final File configFile) {
        if (configuration == null) {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration() {
        propConfigName = configuration.get(Names.Config.Category.GENERAL, Names.Config.FILENAME, CONFIG_NAME_DEFAULT, Names.Config.FILENAME_DESC);
        propConfigName.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.FILENAME);
        propConfigName.setRequiresMcRestart(true);
        configName = propConfigName.getString();

        propReplaceDebug = configuration.get(Names.Config.Category.GENERAL, Names.Config.REPLACE_DEBUG, REPLACE_DEBUG_DEFAULT, Names.Config.REPLACE_DEBUG_DESC);
        propReplaceDebug.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.REPLACE_DEBUG);
        replaceDebug = propReplaceDebug.getBoolean(REPLACE_DEBUG_DEFAULT);

        propShowInChat = configuration.get(Names.Config.Category.GENERAL, Names.Config.SHOW_IN_CHAT, SHOW_IN_CHAT_DEFAULT, Names.Config.SHOW_IN_CHAT_DESC);
        propShowInChat.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SHOW_IN_CHAT);
        showInChat = propShowInChat.getBoolean(SHOW_IN_CHAT_DEFAULT);

        propShowOnPlayerList = configuration.get(Names.Config.Category.GENERAL, Names.Config.SHOW_ON_PLAYER_LIST, SHOW_ON_PLAYER_LIST_DEFAULT, Names.Config.SHOW_ON_PLAYER_LIST_DESC);
        propShowOnPlayerList.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SHOW_ON_PLAYER_LIST);
        showOnPlayerList = propShowOnPlayerList.getBoolean(SHOW_ON_PLAYER_LIST_DEFAULT);

        propScale = configuration.get(Names.Config.Category.GENERAL, Names.Config.SCALE, String.valueOf(SCALE_DEFAULT), Names.Config.SCALE_DESC);
        propScale.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SCALE);
        propScale.setValidValues(new String[] { "0.5", "1.0", "1.5", "2.0" });
        scale = (float) propScale.getDouble(SCALE_DEFAULT);

        propFileInterval = configuration.get(Names.Config.Category.GENERAL, Names.Config.FILE_INTERVAL, FILE_INTERVAL_DEFAULT, Names.Config.FILE_INTERVAL_DESC, 1, 60);
        propFileInterval.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.FILE_INTERVAL);
        fileInterval = propFileInterval.getInt(FILE_INTERVAL_DEFAULT);

        propShowOverlayPotions = configuration.get(Names.Config.Category.GENERAL, Names.Config.SHOW_OVERLAY_POTIONS, SHOW_OVERLAY_POTIONS_DEFAULT, Names.Config.SHOW_OVERLAY_POTIONS_DESC);
        propShowOverlayPotions.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SHOW_OVERLAY_POTIONS);
        showOverlayPotions = propShowOverlayPotions.getBoolean(SHOW_OVERLAY_POTIONS_DEFAULT);

        propShowOverlayItemIcons = configuration.get(Names.Config.Category.GENERAL, Names.Config.SHOW_OVERLAY_ITEM_ICONS, SHOW_OVERLAY_ITEM_ICONS_DEFAULT, Names.Config.SHOW_OVERLAY_ITEM_ICONS_DESC);
        propShowOverlayItemIcons.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SHOW_OVERLAY_ITEM_ICONS);
        showOverlayItemIcons = propShowOverlayItemIcons.getBoolean(SHOW_OVERLAY_ITEM_ICONS_DEFAULT);

        for (final Alignment alignment : Alignment.values()) {
            final String alignmentName = alignment.toString().toLowerCase(Locale.ENGLISH);
            final Property property = configuration.get(Names.Config.Category.ALIGNMENT, alignmentName, alignment.getDefaultXY(), String.format(Names.Config.ALIGNMENT_DESC, alignment.toString()));
            property.setLanguageKey(Names.Config.LANG_PREFIX + "." + alignmentName);
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

    public static void setConfigName(final String name) {
        propConfigName.set(name);
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(Reference.MODID)) {
            loadConfiguration();
        }
    }
}
