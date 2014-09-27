package com.github.lunatrius.ingameinfo.value;

import com.github.lunatrius.core.entity.EntityHelper;
import com.github.lunatrius.ingameinfo.client.gui.InfoIcon;
import com.github.lunatrius.ingameinfo.client.gui.InfoItem;
import com.github.lunatrius.ingameinfo.tag.Tag;
import com.github.lunatrius.ingameinfo.value.registry.ValueRegistry;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

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
                Operation operation = Operation.fromString(getValue(0));
                return operation.getValue(this);
            } catch (Exception e) {
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
            String str = "";
            for (Value val : this.values) {
                str += replaceVariables(val.getValue());
            }
            return str;
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
                double arg0 = getDoubleValue(0);
                double arg1 = getDoubleValue(1);
                int shift = this.values.size() - 2;
                return arg0 < arg1 ? getValue(0 + shift) : getValue(1 + shift);
            } catch (Exception e) {
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
                double arg0 = getDoubleValue(0);
                double arg1 = getDoubleValue(1);
                int shift = this.values.size() - 2;
                return arg0 > arg1 ? getValue(0 + shift) : getValue(1 + shift);
            } catch (Exception e) {
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
                    item = GameData.getItemRegistry().getObject(getValue(0));
                } catch (Exception e3) {
                    item = GameData.getItemRegistry().getObjectById(getIntValue(0));
                }
                if (this.values.size() == 2) {
                    itemDamage = getIntValue(1);
                }
                return String.valueOf(EntityHelper.getItemCountInInventory(Minecraft.getMinecraft().thePlayer.inventory, item, itemDamage));
            } catch (Exception e2) {
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
                String format = getValue(0);
                String[] args = new String[this.values.size() - 1];
                for (int i = 0; i < args.length; i++) {
                    args[i] = getValue(i + 1);
                }
                return I18n.format(format, args);
            } catch (Exception e) {
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
                String format = getValue(0);
                return new SimpleDateFormat(format).format(new Date());
            } catch (Exception e) {
                return "?";
            }
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
                int size = this.values.size();
                String what = getValue(0);

                if (size == 1 || size == 2) {
                    InfoItem item;
                    ItemStack itemStack;

                    int metadata = 0;
                    if (size == 2) {
                        metadata = getIntValue(1);
                        // TODO: this needs a better workaround
                        Block block = GameData.getBlockRegistry().getObject(what);
                        if (block == Blocks.double_plant) {
                            metadata &= 7;
                        }
                    }

                    itemStack = new ItemStack(GameData.getItemRegistry().getObject(what), 1, metadata);
                    if (itemStack.getItem() != null) {
                        item = new InfoItem(Minecraft.getMinecraft().fontRenderer, itemStack);
                        info.add(item);
                        return Tag.getIconTag(item);
                    }

                    itemStack = new ItemStack(GameData.getBlockRegistry().getObject(what), 1, metadata);
                    if (itemStack.getItem() != null) {
                        item = new InfoItem(Minecraft.getMinecraft().fontRenderer, itemStack);
                        info.add(item);
                        return Tag.getIconTag(item);
                    }
                }

                InfoIcon icon = new InfoIcon(what);
                int index = 0;

                if (size == 5 || size == 11) {
                    int displayX = getIntValue(++index);
                    int displayY = getIntValue(++index);
                    int displayWidth = getIntValue(++index);
                    int displayHeight = getIntValue(++index);
                    icon.setDisplayDimensions(displayX, displayY, displayWidth, displayHeight);
                }

                if (size == 7 || size == 11) {
                    int iconX = getIntValue(++index);
                    int iconY = getIntValue(++index);
                    int iconWidth = getIntValue(++index);
                    int iconHeight = getIntValue(++index);
                    int textureWidth = getIntValue(++index);
                    int textureHeight = getIntValue(++index);
                    icon.setTextureData(iconX, iconY, iconWidth, iconHeight, textureWidth, textureHeight);
                }

                info.add(icon);
                return Tag.getIconTag(icon);
            } catch (Exception e) {
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
        ValueRegistry.INSTANCE.register(new ValueIcon().setName("icon").setAliases("img", "image"));
    }
}
