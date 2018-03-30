package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.core.entity.EntityHelper;
import com.github.lunatrius.ingameinfo.client.gui.overlay.InfoItem;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

public abstract class TagPlayerEquipment extends Tag {
    public static final String[] TYPES = new String[] {
            "offhand", "mainhand", "helmet", "chestplate", "leggings", "boots"
    };
    public static final int[] SLOTS = new int[] {
            -2, -1, 3, 2, 1, 0
    };
    protected final int slot;

    public TagPlayerEquipment(final int slot) {
        this.slot = slot;
    }

    @Override
    public String getCategory() {
        return "playerequipment";
    }

    protected ItemStack getItemStack(final int slot) {
        if (slot == -2) {
            return player.getHeldItemOffhand();
        }

        if (slot == -1) {
            return player.getHeldItemMainhand();
        }

        return player.inventory.armorItemInSlot(slot);
    }

    public static class Name extends TagPlayerEquipment {
        public Name(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            if (itemStack.isEmpty()) {
                return "";
            }

            final String arrows;
            if (itemStack.getItem() instanceof ItemBow) {
                final StringBuilder arrowBuilder = new StringBuilder();
                final int regularArrows = EntityHelper.getItemCountInInventory(player.inventory, Items.ARROW);
                final int spectralArrows = EntityHelper.getItemCountInInventory(player.inventory, Items.SPECTRAL_ARROW);
                final int tippedArrows = EntityHelper.getItemCountInInventory(player.inventory, Items.TIPPED_ARROW);

                arrowBuilder.append(" (")
                        .append(Integer.toString(regularArrows + spectralArrows + tippedArrows))
                        .append(")");

                arrows = arrowBuilder.toString();
            } else {
                arrows = "";
            }

            return itemStack.getDisplayName() + arrows;
        }
    }

    public static class UniqueName extends TagPlayerEquipment {
        public UniqueName(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            if (itemStack.isEmpty()) {
                return "";
            }

            return String.valueOf(Item.REGISTRY.getNameForObject(itemStack.getItem()));
        }
    }

    public static class Damage extends TagPlayerEquipment {
        public Damage(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            if (itemStack.isEmpty()) {
                return String.valueOf(-1);
            }

            return String.valueOf(itemStack.isItemStackDamageable() ? itemStack.getItemDamage() : 0);
        }
    }

    public static class MaximumDamage extends TagPlayerEquipment {
        public MaximumDamage(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            if (itemStack.isEmpty()) {
                return String.valueOf(-1);
            }

            return String.valueOf(itemStack.isItemStackDamageable() ? itemStack.getMaxDamage() : 0);
        }
    }

    public static class DamageLeft extends TagPlayerEquipment {
        public DamageLeft(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            if (itemStack.isEmpty()) {
                return String.valueOf(-1);
            }

            return String.valueOf(itemStack.isItemStackDamageable() ? itemStack.getMaxDamage() - itemStack.getItemDamage() : 0);
        }
    }

    public static class Quantity extends TagPlayerEquipment {
        public Quantity(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            if (itemStack.isEmpty()) {
                return String.valueOf(0);
            }

            return String.valueOf(EntityHelper.getItemCountInInventory(player.inventory, itemStack.getItem(), itemStack.getItemDamage()));
        }
    }

    public static class Icon extends TagPlayerEquipment {
        private final boolean large;

        public Icon(final int slot, final boolean large) {
            super(slot);
            this.large = large;
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            final InfoItem item = new InfoItem(itemStack, this.large);
            info.add(item);
            return getIconTag(item);
        }
    }

    public static void register() {
        for (int i = 0; i < TYPES.length; i++) {
            TagRegistry.INSTANCE.register(new Name(SLOTS[i]).setName(TYPES[i] + "name"));
            TagRegistry.INSTANCE.register(new UniqueName(SLOTS[i]).setName(TYPES[i] + "uniquename"));
            TagRegistry.INSTANCE.register(new Damage(SLOTS[i]).setName(TYPES[i] + "damage"));
            TagRegistry.INSTANCE.register(new MaximumDamage(SLOTS[i]).setName(TYPES[i] + "maxdamage"));
            TagRegistry.INSTANCE.register(new DamageLeft(SLOTS[i]).setName(TYPES[i] + "damageleft"));
            TagRegistry.INSTANCE.register(new Quantity(SLOTS[i]).setName(TYPES[i] + "quantity"));
            TagRegistry.INSTANCE.register(new Icon(SLOTS[i], false).setName(TYPES[i] + "icon"));
            TagRegistry.INSTANCE.register(new Icon(SLOTS[i], true).setName(TYPES[i] + "largeicon"));
        }
    }
}
