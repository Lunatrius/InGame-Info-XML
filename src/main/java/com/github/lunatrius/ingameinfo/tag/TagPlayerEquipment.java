package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.core.entity.EntityHelper;
import com.github.lunatrius.ingameinfo.client.gui.overlay.InfoItem;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

public abstract class TagPlayerEquipment extends Tag {
    public static final FMLControlledNamespacedRegistry<Item> ITEM_REGISTRY = GameData.getItemRegistry();
    public static final String[] TYPES = new String[] {
            "equipped", "offhand", "mainhand", "helmet", "chestplate", "leggings", "boots"
    };
    public static final int[] SLOTS = new int[] {
            -3, -2, -1, 3, 2, 1, 0
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

        if (slot == -1 || slot == -3) {
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
            final String arrows = itemStack != null && itemStack.getItem() == Items.BOW ? " (" + EntityHelper.getItemCountInInventory(player.inventory, Items.ARROW) + ")" : "";
            return itemStack != null ? itemStack.getDisplayName() + arrows : "";
        }
    }

    public static class UniqueName extends TagPlayerEquipment {
        public UniqueName(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            final Item item = itemStack != null ? itemStack.getItem() : null;
            return item != null ? String.valueOf(ITEM_REGISTRY.getNameForObject(item)) : "";
        }
    }

    public static class Damage extends TagPlayerEquipment {
        public Damage(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            return String.valueOf(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getItemDamage() : 0);
        }
    }

    public static class MaximumDamage extends TagPlayerEquipment {
        public MaximumDamage(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            return String.valueOf(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getMaxDamage() + 1 : 0);
        }
    }

    public static class DamageLeft extends TagPlayerEquipment {
        public DamageLeft(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            return String.valueOf(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getMaxDamage() + 1 - itemStack.getItemDamage() : 0);
        }
    }

    public static class Quantity extends TagPlayerEquipment {
        public Quantity(final int slot) {
            super(slot);
        }

        @Override
        public String getValue() {
            final ItemStack itemStack = getItemStack(this.slot);
            return String.valueOf(itemStack != null ? EntityHelper.getItemCountInInventory(player.inventory, itemStack.getItem(), itemStack.getItemDamage()) : 0);
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
