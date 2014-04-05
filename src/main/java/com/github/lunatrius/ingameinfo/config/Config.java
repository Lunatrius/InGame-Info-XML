package com.github.lunatrius.ingameinfo.config;

import com.github.lunatrius.core.config.Configuration;
import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.lib.Strings;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Config extends Configuration {
	public final Property configName;
	public final Property showInChat;
	public final Property showOnPlayerList;
	public final Map<Alignment, Property> alignments = new HashMap<Alignment, Property>();

	public Config(File file) {
		super(file);

		this.configName = get(Strings.CONFIG_CATEGORY_GENERAL, Strings.CONFIG_FILENAME, "InGameInfo.xml", Strings.CONFIG_FILENAME_DESC);
		this.showInChat = get(Strings.CONFIG_CATEGORY_GENERAL, Strings.CONFIG_SHOWINCHAT, true, Strings.CONFIG_SHOWINCHAT_DESC);
		this.showOnPlayerList= get(Strings.CONFIG_CATEGORY_GENERAL, Strings.CONFIG_SHOWONPLAYERLIST, true, Strings.CONFIG_SHOWONPLAYERLIST_DESC);

		for (Alignment alignment : Alignment.values()) {
			Property property = get(Strings.CONFIG_CATEGORY_ALIGNMENT, alignment.toString().toLowerCase(), alignment.getXY(), String.format(Strings.CONFIG_ALIGNMENT_DESC, alignment.toString()));
			this.alignments.put(alignment, property);
			alignment.setXY(property.getString());
		}
	}

	public boolean getShowInChat() {
		return this.showInChat.getBoolean(true);
	}

	public boolean getShowOnPlayerList() {
		return this.showOnPlayerList.getBoolean(true);
	}

	public void setConfigName(String filename) {
		this.configName.set(filename);
	}

	public String getConfigName() {
		return this.configName.getString();
	}
}
