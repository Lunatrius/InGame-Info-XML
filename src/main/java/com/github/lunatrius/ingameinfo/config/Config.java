package com.github.lunatrius.ingameinfo.config;

import com.github.lunatrius.core.config.Configuration;
import com.github.lunatrius.ingameinfo.lib.Strings;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config extends Configuration {
	public final Property configName;
	public final Property showInChat;

	public Config(File file) {
		super(file);

		this.configName = get(Strings.CONFIG_CATEGORY, Strings.CONFIG_FILENAME, "InGameInfo.xml", Strings.CONFIG_FILENAME_DESC);
		this.showInChat = get(Strings.CONFIG_CATEGORY, Strings.CONFIG_SHOWINCHAT, true, Strings.CONFIG_SHOWINCHAT_DESC);
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
