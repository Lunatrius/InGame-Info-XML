package com.github.lunatrius.ingameinfo.handler;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.reference.Names;
import com.github.lunatrius.ingameinfo.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigurationHandler {
    public static Configuration configuration;

    public static final String CONFIGNAME_DEFAULT = Names.Files.FILE_XML;
    // TODO: 1.8 - flip the default to true
    public static final boolean REPLACEDEBUG_DEFAULT = false;
    public static final boolean SHOWINCHAT_DEFAULT = true;
    public static final boolean SHOWONPLAYERLIST_DEFAULT = true;
    public static final double SCALE_DEFAULT = 1.0;

    public static String configName = CONFIGNAME_DEFAULT;
    public static boolean replaceDebug = REPLACEDEBUG_DEFAULT;
    public static boolean showInChat = SHOWINCHAT_DEFAULT;
    public static boolean showOnPlayerList = SHOWONPLAYERLIST_DEFAULT;
    public static float scale = (float) SCALE_DEFAULT;

    private static Property propConfigName = null;
    private static Property propReplaceDebug = null;
    private static Property propShowInChat = null;
    private static Property propShowOnPlayerList = null;
    private static Property propScale = null;
    private static final Map<Alignment, Property> propAlignments = new HashMap<Alignment, Property>();

    public static void init(File configFile) {
        if (configuration == null) {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration() {
        propConfigName = configuration.get(Names.Config.Category.GENERAL, Names.Config.FILENAME, CONFIGNAME_DEFAULT, Names.Config.FILENAME_DESC);
        propConfigName.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.FILENAME);
        propConfigName.setRequiresMcRestart(true);
        configName = propConfigName.getString();

        propReplaceDebug = configuration.get(Names.Config.Category.GENERAL, Names.Config.REPLACEDEBUG, REPLACEDEBUG_DEFAULT, Names.Config.REPLACEDEBUG_DESC);
        propReplaceDebug.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.REPLACEDEBUG);
        replaceDebug = propReplaceDebug.getBoolean(REPLACEDEBUG_DEFAULT);

        propShowInChat = configuration.get(Names.Config.Category.GENERAL, Names.Config.SHOWINCHAT, SHOWINCHAT_DEFAULT, Names.Config.SHOWINCHAT_DESC);
        propShowInChat.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SHOWINCHAT);
        showInChat = propShowInChat.getBoolean(SHOWINCHAT_DEFAULT);

        propShowOnPlayerList = configuration.get(Names.Config.Category.GENERAL, Names.Config.SHOWONPLAYERLIST, SHOWONPLAYERLIST_DEFAULT, Names.Config.SHOWONPLAYERLIST_DESC);
        propShowOnPlayerList.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SHOWONPLAYERLIST);
        showOnPlayerList = propShowOnPlayerList.getBoolean(SHOWONPLAYERLIST_DEFAULT);

        propScale = configuration.get(Names.Config.Category.GENERAL, Names.Config.SCALE, String.valueOf(SCALE_DEFAULT), Names.Config.SCALE_DESC);
        propScale.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SCALE);
        propScale.setValidValues(new String[] { "0.5", "1.0", "1.5", "2.0" });
        scale = (float) propScale.getDouble(SCALE_DEFAULT);

        for (Alignment alignment : Alignment.values()) {
            Property property = configuration.get(Names.Config.Category.ALIGNMENT, alignment.toString().toLowerCase(), alignment.getXY(), String.format(Names.Config.ALIGNMENT_DESC, alignment.toString()));
            property.setLanguageKey(Names.Config.LANG_PREFIX + "." + alignment.toString().toLowerCase());
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
