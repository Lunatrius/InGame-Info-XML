package com.github.lunatrius.ingameinfo.reference;

import com.github.lunatrius.ingameinfo.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference {
    public static final String MODID = "ingameinfoxml";
    public static final String NAME = "InGame Info XML";
    public static final String VERSION = Tags.VERSION;
    public static final String FORGE = "14.23.5.2847";
    public static final String MINECRAFT = "1.12.2";
    public static final String PROXY_SERVER = "com.github.lunatrius.ingameinfo.proxy.ServerProxy";
    public static final String PROXY_CLIENT = "com.github.lunatrius.ingameinfo.proxy.ClientProxy";
    public static final String GUI_FACTORY = "com.github.lunatrius.ingameinfo.client.gui.config.GuiFactory";

    public static Logger logger = LogManager.getLogger(Reference.MODID);
}
