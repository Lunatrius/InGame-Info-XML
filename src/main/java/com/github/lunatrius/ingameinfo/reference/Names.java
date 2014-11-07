package com.github.lunatrius.ingameinfo.reference;

@SuppressWarnings("HardCodedStringLiteral")
public final class Names {
    public static final class Mods {
        public static final String SIMPLYJETPACKS_MODID = "simplyjetpacks";
        public static final String SIMPLYJETPACKS_NAME = "Simply Jetpacks";

        public static final String THAUMCRAFT_MODID = "Thaumcraft";
        public static final String THAUMCRAFT_NAME = "Thaumcraft";
    }

    public static final class Command {
        public static final class Message {
            public static final String USAGE = "commands.ingameinfoxml.usage";
            public static final String RELOAD = "commands.ingameinfoxml.reload";
            public static final String LOAD = "commands.ingameinfoxml.load";
            public static final String SAVE = "commands.ingameinfoxml.save";
            public static final String SUCCESS = "commands.ingameinfoxml.success";
            public static final String FAILURE = "commands.ingameinfoxml.failure";
            public static final String ENABLE = "commands.ingameinfoxml.enable";
            public static final String DISABLE = "commands.ingameinfoxml.disable";
        }

        public static final String NAME = "igi";
        public static final String RELOAD = "reload";
        public static final String LOAD = "load";
        public static final String SAVE = "save";
        public static final String ENABLE = "enable";
        public static final String DISABLE = "disable";
        public static final String TAGLIST = "taglist";
        public static final String CONFIG = "config";
    }

    public static final class Config {
        public static final class Category {
            public static final String GENERAL = "general";
            public static final String ALIGNMENT = "alignment";
        }

        public static final String FILENAME = "filename";
        public static final String FILENAME_DESC = "The configuration that that should be loaded on startup.";
        public static final String REPLACEDEBUG = "replaceDebug";
        public static final String REPLACEDEBUG_DESC = "Replace the debug overlay (F3) with the InGameInfoXML overlay.";
        public static final String SHOWINCHAT = "showInChat";
        public static final String SHOWINCHAT_DESC = "Display the overlay in chat.";
        public static final String SHOWONPLAYERLIST = "showOnPlayerList";
        public static final String SHOWONPLAYERLIST_DESC = "Display the overlay on the player list.";
        public static final String SCALE = "scale";
        public static final String SCALE_DESC = "The overlay will be scaled by this amount.";

        public static final String ALIGNMENT_DESC = "Offsets for %s (X<space>Y).";

        public static final String LANG_PREFIX = Reference.MODID.toLowerCase() + ".config";
    }

    public static final class Files {
        public static final String NAME = "InGameInfo";

        public static final String FILE_XML = "InGameInfo.xml";
        public static final String FILE_JSON = "InGameInfo.json";
        public static final String FILE_TXT = "InGameInfo.txt";

        public static final String EXT_XML = ".xml";
        public static final String EXT_JSON = ".json";
        public static final String EXT_TXT = ".txt";
    }
}
