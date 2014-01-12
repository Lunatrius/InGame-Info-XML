package com.github.lunatrius.ingameinfo;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.io.File;

public class Config extends Configuration {
	private Property configName = null;
	private Property showInChat = null;

	public Config(File file) {
		super(file);
	}

	public void load() {
		super.load();
		this.configName = get(Configuration.CATEGORY_GENERAL, "filename", "InGameInfo.xml", "The configuration that that should be loaded on startup.");
		this.showInChat = get(Configuration.CATEGORY_GENERAL, "showInChat", true, "Displays the overlay in chat.");
	}

	public boolean getShowInChat() {
		return this.showInChat.getBoolean(true);
	}

	public void setConfigName(String filename) {
		this.configName.set(filename);
	}

	public String getConfigName() {
		return this.configName.getString();
	}
}
