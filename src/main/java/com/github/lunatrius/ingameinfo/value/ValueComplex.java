package com.github.lunatrius.ingameinfo.value;

import com.github.lunatrius.core.entity.EntityHelper;
import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.client.gui.overlay.InfoIcon;
import com.github.lunatrius.ingameinfo.client.gui.overlay.InfoItem;
import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.tag.Tag;
import com.github.lunatrius.ingameinfo.value.registry.ValueRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class ValueComplex extends Value {
    @Override
    public boolean isSimple() {
        return false;
    }

    public static class ValueOperation extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() > 1;
        }

        @Override
        public String getValue() {
            try {
                final Operation operation = Operation.fromString(getValue(0));
                return operation.getValue(this);
            } catch (final Exception e) {
                return "";
            }
        }
    }

    public static class ValueConcat extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() > 1;
        }

        @Override
        public String getValue() {
            StringBuilder str = new StringBuilder();
            for (final Value val : this.values) {
                str.append(replaceVariables(val.getValue()));
            }
            return str.toString();
        }
    }

    public static class ValueMin extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() == 2 || this.values.size() == 4;
        }

        @Override
        public String getValue() {
            try {
                final double arg0 = getDoubleValue(0);
                final double arg1 = getDoubleValue(1);
                final int shift = this.values.size() - 2;
                return arg0 <= arg1 ? getValue(shift) : getValue(1 + shift);
            } catch (final Exception e) {
                return "0";
            }
        }
    }

    public static class ValueMax extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() == 2 || this.values.size() == 4;
        }

        @Override
        public String getValue() {
            try {
                final double arg0 = getDoubleValue(0);
                final double arg1 = getDoubleValue(1);
                final int shift = this.values.size() - 2;
                return arg0 >= arg1 ? getValue(shift) : getValue(1 + shift);
            } catch (final Exception e) {
                return "0";
            }
        }
    }

    public static class ValueItemQuantity extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() == 1 || this.values.size() == 2;
        }

        @Override
        public String getValue() {
            try {
                Item item;
                int itemDamage = -1;
                try {
                    item = Item.REGISTRY.getObject(new ResourceLocation(getValue(0)));
                } catch (final Exception e3) {
                    item = Item.REGISTRY.getObjectById(getIntValue(0));
                }
                if (this.values.size() == 2) {
                    itemDamage = getIntValue(1);
                }
                return String.valueOf(EntityHelper.getItemCountInInventory(Minecraft.getMinecraft().player.inventory, item, itemDamage));
            } catch (final Exception e2) {
                return "0";
            }
        }
    }

    public static class ValueTranslate extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() > 0;
        }

        @Override
        public String getValue() {
            try {
                final String format = getValue(0);
                final String[] args = new String[this.values.size() - 1];
                for (int i = 0; i < args.length; i++) {
                    args[i] = getValue(i + 1);
                }
                return I18n.format(format, args);
            } catch (final Exception e) {
                return "?";
            }
        }
    }

    public static class ValueFormattedTime extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() == 1;
        }

        @Override
        public String getValue() {
            try {
                final String format = getValue(0);
                return new SimpleDateFormat(format).format(new Date());
            } catch (final Exception e) {
                return "?";
            }
        }
    }

    public static class ValueFormattedNumber extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() == 2;
        }

        @Override
        public String getValue() {
            try {
                final String format = getValue(0);
                return new DecimalFormat(format).format(getDoubleValue(1));
            } catch (final Exception e) {
                return "?";
            }
        }
    }

    public static class ValueFile extends ValueComplex {
        private static int ticks = 0;

        private Map<String, String> cache = new HashMap<>();

        @Override
        public boolean isValidSize() {
            return this.values.size() == 1;
        }

        @Override
        public String getValue() {
            final String filename = getValue(0);

            if (ticks == 0) {
                if (this.cache.size() > 16) {
                    Reference.logger.trace("Clearing file cache...");
                    this.cache.clear();
                }

                final File file = new File(InGameInfoCore.INSTANCE.getConfigDirectory(), filename);
                if (file.exists()) {
                    this.cache.put(filename, getLine(file));
                }
            }

            final String line = this.cache.get(filename);
            if (line != null) {
                return line;
            }

            return "";
        }

        private String getLine(final File file) {
            try {
                final FileReader fileReader = new FileReader(file);
                final BufferedReader reader = new BufferedReader(fileReader);

                final String line = reader.readLine();

                reader.close();

                if (line.startsWith("\uFEFF")) {
                    return line.substring(1);
                }

                return line;
            } catch (final Exception e) {
                Reference.logger.error("", e);
            }

            return "";
        }

        public static void tick() {
            ticks = (ticks + 1) % (ConfigurationHandler.fileInterval * 20);
        }
    }

    public static class ValueIcon extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return this.values.size() == 1 || this.values.size() == 2 || this.values.size() == 5 || this.values.size() == 7 || this.values.size() == 11;
        }

        @Override
        public String getValue() {
            try {
                final int size = this.values.size();
                final ResourceLocation what = new ResourceLocation(getValue(0));

                if (size == 1 || size == 2) {
                    final InfoItem item;
                    ItemStack itemStack;

                    int metadata = 0;
                    if (size == 2) {
                        metadata = getIntValue(1);
                        // TODO: this needs a better workaround
                        final Block block = Block.REGISTRY.getObject(what);
                        if (block == Blocks.DOUBLE_PLANT) {
                            metadata &= 7;
                        }
                    }

                    itemStack = new ItemStack(Item.REGISTRY.getObject(what), 1, metadata);
                    if (itemStack.getItem() != null) {
                        item = new InfoItem(itemStack);
                        info.add(item);
                        return Tag.getIconTag(item);
                    }

                    itemStack = new ItemStack(Block.REGISTRY.getObject(what), 1, metadata);
                    if (itemStack.getItem() != null) {
                        item = new InfoItem(itemStack);
                        info.add(item);
                        return Tag.getIconTag(item);
                    }
                }

                final InfoIcon icon = new InfoIcon(what);
                int index = 0;

                if (size == 5 || size == 11) {
                    final int displayX = getIntValue(++index);
                    final int displayY = getIntValue(++index);
                    final int displayWidth = getIntValue(++index);
                    final int displayHeight = getIntValue(++index);
                    icon.setDisplayDimensions(displayX, displayY, displayWidth, displayHeight);
                }

                if (size == 7 || size == 11) {
                    final int iconX = getIntValue(++index);
                    final int iconY = getIntValue(++index);
                    final int iconWidth = getIntValue(++index);
                    final int iconHeight = getIntValue(++index);
                    final int textureWidth = getIntValue(++index);
                    final int textureHeight = getIntValue(++index);
                    icon.setTextureData(iconX, iconY, iconWidth, iconHeight, textureWidth, textureHeight);
                }

                info.add(icon);
                return Tag.getIconTag(icon);
            } catch (final Exception e) {
                return "?";
            }
        }
    }

    public static void register() {
        ValueRegistry.INSTANCE.register(new ValueOperation().setName("op").setAliases("operation"));
        ValueRegistry.INSTANCE.register(new ValueConcat().setName("concat"));
        ValueRegistry.INSTANCE.register(new ValueMax().setName("max").setAliases("maximum"));
        ValueRegistry.INSTANCE.register(new ValueMin().setName("min").setAliases("minimum"));
        ValueRegistry.INSTANCE.register(new ValueItemQuantity().setName("itemquantity"));
        ValueRegistry.INSTANCE.register(new ValueTranslate().setName("trans").setAliases("translate"));
        ValueRegistry.INSTANCE.register(new ValueFormattedTime().setName("formattedtime").setAliases("rltimef"));
        ValueRegistry.INSTANCE.register(new ValueFormattedNumber().setName("formattednumber"));
        ValueRegistry.INSTANCE.register(new ValueIcon().setName("icon").setAliases("img", "image"));
        ValueRegistry.INSTANCE.register(new ValueFile().setName("file"));
    }
}
