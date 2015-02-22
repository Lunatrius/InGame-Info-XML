package com.github.lunatrius.ingameinfo.integration.simplyjetpacks.tag;

import cofh.api.energy.IEnergyContainerItem;
import com.github.lunatrius.ingameinfo.tag.TagIntegration;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.item.ItemStack;
import tonius.simplyjetpacks.item.ItemPack;
import tonius.simplyjetpacks.item.meta.Jetpack;

import java.util.Locale;

public abstract class TagSimplyJetpacks extends TagIntegration {
    @Override
    public String getCategory() {
        return "simplyjetpacks";
    }

    public static class Energy extends TagSimplyJetpacks {
        @Override
        public String getValue() {
            try {
                final ItemStack chestplate = player.getCurrentArmor(2);
                if (chestplate != null) {
                    if (chestplate.getItem() instanceof IEnergyContainerItem) {
                        final IEnergyContainerItem item = (IEnergyContainerItem) chestplate.getItem();
                        return String.valueOf(item.getEnergyStored(chestplate));
                    }
                }
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class MaxEnergy extends TagSimplyJetpacks {
        @Override
        public String getValue() {
            try {
                final ItemStack chestplate = player.getCurrentArmor(2);
                if (chestplate != null) {
                    if (chestplate.getItem() instanceof IEnergyContainerItem) {
                        final IEnergyContainerItem item = (IEnergyContainerItem) chestplate.getItem();
                        return String.valueOf(item.getMaxEnergyStored(chestplate));
                    }
                }
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class Percent extends TagSimplyJetpacks {
        @Override
        public String getValue() {
            try {
                final ItemStack chestplate = player.getCurrentArmor(2);
                if (chestplate != null) {
                    if (chestplate.getItem() instanceof IEnergyContainerItem) {
                        final IEnergyContainerItem item = (IEnergyContainerItem) chestplate.getItem();
                        return String.format(Locale.ENGLISH, "%.2f", 100.0 * item.getEnergyStored(chestplate) / item.getMaxEnergyStored(chestplate));
                    }
                }
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class Enabled extends TagSimplyJetpacks {
        @Override
        public String getValue() {
            try {
                final ItemStack chestplate = player.getCurrentArmor(2);
                if (chestplate != null) {
                    if (chestplate.getItem() instanceof ItemPack.ItemJetpack) {
                        final ItemPack.ItemJetpack item = (ItemPack.ItemJetpack) chestplate.getItem();
                        final Jetpack jetpack = item.getPack(chestplate);
                        return String.valueOf(jetpack != null && jetpack.isOn(chestplate));
                    }
                }
            } catch (Throwable e) {
                log(this, e);
            }
            return String.valueOf(false);
        }
    }

    public static class Hover extends TagSimplyJetpacks {
        @Override
        public String getValue() {
            try {
                final ItemStack chestplate = player.getCurrentArmor(2);
                if (chestplate != null) {
                    if (chestplate.getItem() instanceof ItemPack.ItemJetpack) {
                        final ItemPack.ItemJetpack item = (ItemPack.ItemJetpack) chestplate.getItem();
                        final Jetpack jetpack = item.getPack(chestplate);
                        return String.valueOf(jetpack != null && jetpack.isHoverModeOn(chestplate));
                    }
                }
            } catch (Throwable e) {
                log(this, e);
            }
            return String.valueOf(false);
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new Energy().setName("sjenergy"));
        TagRegistry.INSTANCE.register(new MaxEnergy().setName("sjmaxenergy"));
        TagRegistry.INSTANCE.register(new Percent().setName("sjpercent"));
        TagRegistry.INSTANCE.register(new Enabled().setName("sjenabled"));
        TagRegistry.INSTANCE.register(new Hover().setName("sjhover"));
    }
}
