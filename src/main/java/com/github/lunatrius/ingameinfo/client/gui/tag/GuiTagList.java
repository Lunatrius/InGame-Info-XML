package com.github.lunatrius.ingameinfo.client.gui.tag;

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

    public GuiTagList(final GuiTags guiTags, final Minecraft minecraft) {
        super(minecraft, guiTags.width, guiTags.height, 18, guiTags.height - 30, 24);
        this.minecraft = minecraft;

        this.map = new TreeMap<>();

        final Map<String, CategoryEntry> stringCategoryEntryMap = new HashMap<>();
        for (final Tag tag : TagRegistry.INSTANCE.getRegisteredTags()) {
            final String category = tag.getLocalizedCategory();
            final String name = tag.getFormattedName();
            final String description = tag.getLocalizedDescription();

            CategoryEntry categoryEntry = stringCategoryEntryMap.get(category);
            if (categoryEntry == null) {
                categoryEntry = new CategoryEntry(this.minecraft.fontRenderer, category);
                stringCategoryEntryMap.put(category, categoryEntry);
                this.map.put(categoryEntry, new TreeSet<>());
            }
            final Set<TagEntry> tagEntries = this.map.get(categoryEntry);
            if (tagEntries != null) {
                tagEntries.add(new TagEntry(this.minecraft.fontRenderer, name, description));
            }
        }

        filter("");
    }

    public void filter(final String pattern) {
        final List<IGuiListEntry> list = new ArrayList<>();
        for (final Map.Entry<CategoryEntry, Set<TagEntry>> entry : this.map.entrySet()) {
            list.add(entry.getKey());

            boolean added = false;
            for (final TagEntry tag : entry.getValue()) {
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
    public IGuiListEntry getListEntry(final int index) {
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
        public boolean mousePressed(final int index, final int x, final int y, final int mouseEvent, final int relativeX, final int relativeY) {
            return false;
        }

        @Override
        public void mouseReleased(final int index, final int x, final int y, final int mouseEvent, final int relativeX, final int relativeY) {
        }

        @Override
        public void updatePosition(final int p_192633_1_, final int p_192633_2_, final int p_192633_3_, final float p_192633_4_) {
        }

        public abstract String getName();

        @Override
        public int compareTo(final ListEntry listEntry) {
            return getName().compareTo(listEntry.getName());
        }
    }

    public class CategoryEntry extends ListEntry {
        private final FontRenderer fontRenderer;
        private final String name;

        public CategoryEntry(final FontRenderer fontRenderer, final String name) {
            this.fontRenderer = fontRenderer;
            this.name = name;
        }

        @Override
        public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected, final float partialTicks) {
            this.fontRenderer.drawString(this.name, x + (listWidth - this.fontRenderer.getStringWidth(this.name)) / 2, y + (slotHeight - this.fontRenderer.FONT_HEIGHT + 1) / 2, 0xFFFFFF);
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

        public TagEntry(final FontRenderer fontRenderer, final String name, final String desc) {
            this.fontRenderer = fontRenderer;
            this.name = name;
            this.desc = desc;
            this.descArray = getDescArray(desc);
        }

        private String[] getDescArray(String desc) {
            final List<String> list = new ArrayList<>();

            final int width = getListWidth() - OFFSET_X - SCROLLBAR_WIDTH;
            if (this.fontRenderer.getStringWidth(desc) < width) {
                list.add(desc);
            } else {
                while (this.fontRenderer.getStringWidth(desc) > width) {
                    final String trimmed = this.fontRenderer.trimStringToWidth(desc, width);
                    final int index = trimmed.lastIndexOf(" ");
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
        public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected, final float partialTicks) {
            this.fontRenderer.drawString(this.name, x, y + slotHeight / 2 - this.fontRenderer.FONT_HEIGHT / 2, 0xFFFFFF);

            final int lineHeight = this.fontRenderer.FONT_HEIGHT + 1;
            for (int i = 0; i < this.descArray.length; i++) {
                this.fontRenderer.drawString(this.descArray[i], x + OFFSET_X, y + (slotHeight - lineHeight * this.descArray.length) / 2 + lineHeight * i, 0xFFFFFF);
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
