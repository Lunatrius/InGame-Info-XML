package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.core.entity.EntityHelper;
import com.github.lunatrius.ingameinfo.client.gui.InfoItem;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

public abstract class TagPlayerEquipment extends Tag {
    public static final FMLControlledNamespacedRegistry<Item> ITEM_REGISTRY = GameData.getItemRegistry();
    public static final String[] TYPES = new String[] { "equipped", "helmet", "chestplate", "leggings", "boots" };
    public static final int[] SLOTS = new int[] { -1, 3, 2, 1, 0 };
    protected final int slot;

    public TagPlayerEquipment(int slot) {
        this.slot = slot;
    }

    @Override
    public String getCategory() {
        return "playerequipment";
    }

    protected ItemStack getItemStack(int slot) {
        if (slot == -1) {
            return player.getCurrentEquippedItem();
        }
        return player.inventory.armorItemInSlot(slot);
    }

    public static class Name extends TagPlayerEquipment {
        public Name(int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            ItemStack itemStack = getItemStack(this.slot);
            String arrows = itemStack != null && itemStack.getItem() == Items.bow ? " (" + EntityHelper.getItemCountInInventory(player.inventory, Items.arrow) + ")" : "";
            return itemStack != null ? itemStack.getDisplayName() + arrows : "";
        }
    }

    public static class UniqueName extends TagPlayerEquipment {
        public UniqueName(int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            ItemStack itemStack = getItemStack(this.slot);
            Item item = itemStack != null ? itemStack.getItem() : null;
            return item != null ? String.valueOf(ITEM_REGISTRY.getNameForObject(item)) : "";
        }
    }

    public static class Damage extends TagPlayerEquipment {
        public Damage(int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            ItemStack itemStack = getItemStack(this.slot);
            return String.valueOf(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getItemDamage() : 0);
        }
    }

    public static class MaximumDamage extends TagPlayerEquipment {
        public MaximumDamage(int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            ItemStack itemStack = getItemStack(this.slot);
            return String.valueOf(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getMaxDamage() + 1 : 0);
        }
    }

    public static class DamageLeft extends TagPlayerEquipment {
        public DamageLeft(int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            ItemStack itemStack = getItemStack(this.slot);
            return String.valueOf(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getMaxDamage() + 1 - itemStack.getItemDamage() : 0);
        }
    }

    public static class Quantity extends TagPlayerEquipment {
        public Quantity(int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            ItemStack itemStack = getItemStack(this.slot);
            return String.valueOf(itemStack != null ? EntityHelper.getItemCountInInventory(player.inventory, itemStack.getItem(), itemStack.getItemDamage()) : 0);
        }
    }

    public static class Icon extends TagPlayerEquipment {
        private final boolean large;

        public Icon(int slot, boolean large) {
            super(slot);
            this.large = large;
        }

        @Override
        public String getValue() {
            ItemStack itemStack = getItemStack(this.slot);
            InfoItem item = new InfoItem(itemStack, this.large);
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
