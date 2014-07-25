package com.github.lunatrius.ingameinfo.reference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference {
	public static final String MODID = "InGameInfoXML";
	public static final String NAME = "InGame Info XML";
	public static final String VERSION = "${version}";
	public static final String FORGE = "${forgeversion}";
	public static final String MINECRAFT = "${mcversion}";
	public static final String PROXY_COMMON = "com.github.lunatrius.ingameinfo.proxy.CommonProxy";
	public static final String PROXY_CLIENT = "com.github.lunatrius.ingameinfo.proxy.ClientProxy";
	public static final String GUI_FACTORY = "com.github.lunatrius.ingameinfo.client.gui.GuiFactory";

	public static Logger logger = LogManager.getLogger(Reference.MODID);
}
