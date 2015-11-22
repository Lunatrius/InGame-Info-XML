package com.github.lunatrius.ingameinfo.client.gui;

import com.github.lunatrius.ingameinfo.tag.Tag;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class GuiTagList extends GuiListExtended {
    public static final int OFFSET_X = 150;
    public static final int SCROLLBAR_WIDTH = 6;

    private final Minecraft minecraft;
    private final Map<CategoryEntry, Set<TagEntry>> map;
    private IGuiListEntry[] entries;

    public GuiTagList(GuiTags guiTags, Minecraft minecraft) {
        super(minecraft, guiTags.width, guiTags.height, 18, guiTags.height - 30, 24);
        this.minecraft = minecraft;

        this.map = new TreeMap<CategoryEntry, Set<TagEntry>>();

        Map<String, CategoryEntry> stringCategoryEntryMap = new HashMap<String, CategoryEntry>();
        for (Tag tag : TagRegistry.INSTANCE.getRegisteredTags()) {
            String category = tag.getLocalizedCategory();
            String name = tag.getFormattedName();
            String description = tag.getLocalizedDescription();

            CategoryEntry categoryEntry = stringCategoryEntryMap.get(category);
            if (categoryEntry == null) {
                categoryEntry = new CategoryEntry(this.minecraft.fontRendererObj, category);
                stringCategoryEntryMap.put(category, categoryEntry);
                this.map.put(categoryEntry, new TreeSet<TagEntry>());
            }
            Set<TagEntry> tagEntries = this.map.get(categoryEntry);
            if (tagEntries != null) {
                tagEntries.add(new TagEntry(this.minecraft.fontRendererObj, name, description));
            }
        }

        filter("");
    }

    public void filter(String pattern) {
        List<IGuiListEntry> list = new ArrayList<IGuiListEntry>();
        for (Map.Entry<CategoryEntry, Set<TagEntry>> entry : this.map.entrySet()) {
            list.add(entry.getKey());

            boolean added = false;
            for (TagEntry tag : entry.getValue()) {
                if (tag.getName().toLowerCase(Locale.ENGLISH).contains(pattern) || tag.getDesc().toLowerCase(Locale.ENGLISH).contains(pattern)) {
                    added = true;
                    list.add(tag);
                }
            }

            if (!added) {
                list.remove(list.size() - 1);
            }
        }

        this.entries = list.toArray(new IGuiListEntry[list.size()]);
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return this.entries[index];
    }

    @Override
    protected int getSize() {
        return this.entries.length;
    }

    @Override
    public int getListWidth() {
        return Math.min(Math.max(this.width, 400), 440);
    }

    @Override
    protected int getScrollBarX() {
        return this.width / 2 + getListWidth() / 2 - SCROLLBAR_WIDTH;
    }

    public abstract class ListEntry implements IGuiListEntry, Comparable<ListEntry> {
        @Override
        public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            return false;
        }

        @Override
        public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }

        @Override
        public void setSelected(int index, int p_178011_2_, int p_178011_3_) {
        }

        public abstract String getName();

        @Override
        public int compareTo(ListEntry listEntry) {
            return getName().compareTo(listEntry.getName());
        }
    }

    public class CategoryEntry extends ListEntry {
        private final FontRenderer fontRenderer;
        private final String name;

        public CategoryEntry(FontRenderer fontRenderer, String name) {
            this.fontRenderer = fontRenderer;
            this.name = name;
        }

        @Override
        public void drawEntry(int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean isSelected) {
            this.fontRenderer.drawString(this.name, x + (width - this.fontRenderer.getStringWidth(this.name)) / 2, y + (height - this.fontRenderer.FONT_HEIGHT + 1) / 2, 0xFFFFFF);
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public class TagEntry extends ListEntry {
        private final FontRenderer fontRenderer;
        private final String name;
        private final String desc;
        private final String[] descArray;

        public TagEntry(FontRenderer fontRenderer, String name, String desc) {
            this.fontRenderer = fontRenderer;
            this.name = name;
            this.desc = desc;
            this.descArray = getDescArray(desc);
        }

        private String[] getDescArray(String desc) {
            List<String> list = new ArrayList<String>();

            int width = getListWidth() - OFFSET_X - SCROLLBAR_WIDTH;
            if (this.fontRenderer.getStringWidth(desc) < width) {
                list.add(desc);
            } else {
                while (this.fontRenderer.getStringWidth(desc) > width) {
                    String trimmed = this.fontRenderer.trimStringToWidth(desc, width);
                    int index = trimmed.lastIndexOf(" ");
                    if (index < 1) {
                        break;
                    }
                    desc = desc.substring(index + 1);
                    list.add(trimmed.substring(0, index));
                }
                list.add(desc);
            }

            return list.toArray(new String[list.size()]);
        }

        @Override
        public void drawEntry(int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean isSelected) {
            this.fontRenderer.drawString(this.name, x, y + height / 2 - this.fontRenderer.FONT_HEIGHT / 2, 0xFFFFFF);

            int lineHeight = this.fontRenderer.FONT_HEIGHT + 1;
            for (int i = 0; i < this.descArray.length; i++) {
                this.fontRenderer.drawString(this.descArray[i], x + OFFSET_X, y + (height - lineHeight * this.descArray.length) / 2 + lineHeight * i, 0xFFFFFF);
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        public String getDesc() {
            return this.desc;
        }
    }
}
