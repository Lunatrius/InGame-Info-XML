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
import com.github.lunatrius.ingameinfo.tag.TagRiding;
import com.github.lunatrius.ingameinfo.tag.TagTime;
import com.github.lunatrius.ingameinfo.tag.TagWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        this.stringTagMap.put(name.toLowerCase(Locale.ENGLISH), tag);
    }

    public void register(Tag tag) {
        register(tag.getName(), tag);

        for (String name : tag.getAliases()) {
            register(name, tag);
        }
    }

    public String getValue(String name) {
        Tag tag = this.stringTagMap.get(name.toLowerCase(Locale.ENGLISH));
        return tag != null ? tag.getValue() : null;
    }

    public List<Tag> getRegisteredTags() {
        List<Tag> tags = new ArrayList<Tag>();
        for (Map.Entry<String, Tag> entry : this.stringTagMap.entrySet()) {
            tags.add(entry.getValue());
        }
        return tags;
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
        TagRiding.register();
        TagTime.register();
        TagWorld.register();

        Reference.logger.info("Registered " + this.stringTagMap.size() + " tags.");
    }
}
