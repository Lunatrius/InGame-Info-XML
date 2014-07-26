package com.github.lunatrius.ingameinfo.tag.registry;

import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.tag.Tag;
import com.github.lunatrius.ingameinfo.tag.TagFormatting;
import com.github.lunatrius.ingameinfo.tag.TagMisc;
import com.github.lunatrius.ingameinfo.tag.TagMouseOver;
import com.github.lunatrius.ingameinfo.tag.TagNearbyPlayer;
import com.github.lunatrius.ingameinfo.tag.TagPlayerEquipment;
import com.github.lunatrius.ingameinfo.tag.TagPlayerGeneral;
import com.github.lunatrius.ingameinfo.tag.TagPlayerPosition;
import com.github.lunatrius.ingameinfo.tag.TagPlayerPotion;
import com.github.lunatrius.ingameinfo.tag.TagTime;
import com.github.lunatrius.ingameinfo.tag.TagWorld;

import java.util.HashMap;
import java.util.Map;

public class TagRegistry {
	public static final TagRegistry INSTANCE = new TagRegistry();

	private Map<String, Tag> stringTagMap = new HashMap<String, Tag>();

	private void register(String name, Tag tag) {
		if (this.stringTagMap.containsKey(name)) {
			Reference.logger.error("Duplicate tag key '" + name + "'!");
			return;
		}

		if (name == null) {
			Reference.logger.error("Tag name cannot be null!");
			return;
		}

		this.stringTagMap.put(name.toLowerCase(), tag);
	}

	public void register(Tag tag, String name, String... extra) {
		register(name, tag);

		for (String n : extra) {
			register(n, tag);
		}
	}

	public String getValue(String name) {
		Tag tag = this.stringTagMap.get(name.toLowerCase());
		return tag != null ? tag.getValue() : null;
	}

	public void init() {
		TagFormatting.register();
		TagMisc.register();
		TagMouseOver.register();
		TagNearbyPlayer.register();
		TagPlayerEquipment.register();
		TagPlayerGeneral.register();
		TagPlayerPosition.register();
		TagPlayerPotion.register();
		TagTime.register();
		TagWorld.register();

		Reference.logger.info("Registered " + this.stringTagMap.size() + " tags.");
	}
}
