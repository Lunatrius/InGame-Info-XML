package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.client.gui.overlay.InfoIcon;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Collection;

public abstract class TagPlayerPotion extends Tag {
    // TODO: this shouldn't be hardcoded...
    public static final int MAXIMUM_INDEX = 32 /* Potion.potionTypes.length */;

    protected static PotionEffect[] potionEffects = null;
    protected final int index;

    public TagPlayerPotion(final int index) {
        this.index = index;
    }

    @Override
    public String getName() {
        return super.getName() + this.index;
    }

    @Override
    public String[] getAliases() {
        final String[] aliases = super.getAliases();
        final String[] aliasesIndexed = new String[aliases.length];
        for (int i = 0; i < aliases.length; i++) {
            aliasesIndexed[i] = aliases[i] + this.index;
        }
        return aliasesIndexed;
    }

    @Override
    public boolean isIndexed() {
        return true;
    }

    @Override
    public int getMaximumIndex() {
        return MAXIMUM_INDEX - 1;
    }

    @Override
    public String getCategory() {
        return "playerpotion";
    }

    protected void updatePotionEffects() {
        if (potionEffects == null) {
            final Collection<PotionEffect> potionEffectCollection = player.getActivePotionEffects();
            potionEffects = new PotionEffect[potionEffectCollection.size()];
            if (potionEffectCollection.size() > 0) {
                int index = 0;

                for (final PotionEffect potionEffect : potionEffectCollection) {
                    potionEffects[index++] = potionEffect;
                }
            }
        }
    }

    public static class Effect extends TagPlayerPotion {
        public Effect(final int index) {
            super(index);
        }

        @Override
        public String getValue() {
            updatePotionEffects();
            if (potionEffects.length > this.index) {
                String str = I18n.format(potionEffects[this.index].getEffectName());
                switch (potionEffects[this.index].getAmplifier()) {
                case 1:
                    str += " II";
                    break;
                case 2:
                    str += " III";
                    break;
                case 3:
                    str += " IV";
                    break;
                }
                return str;
            }
            return "";
        }
    }

    public static class Duration extends TagPlayerPotion {
        public Duration(final int index) {
            super(index);
        }

        @Override
        public String getValue() {
            updatePotionEffects();
            if (potionEffects.length > this.index) {
                return Potion.getPotionDurationString(potionEffects[this.index], 1);
            }
            return "0:00";
        }
    }

    public static class DurationTicks extends TagPlayerPotion {
        public DurationTicks(final int index) {
            super(index);
        }

        @Override
        public String getValue() {
            updatePotionEffects();
            if (potionEffects.length > this.index) {
                return String.valueOf(potionEffects[this.index].getDuration());
            }
            return "0";
        }
    }

    public static class Negative extends TagPlayerPotion {
        public Negative(final int index) {
            super(index);
        }

        @Override
        public String getValue() {
            updatePotionEffects();
            if (potionEffects.length > this.index) {
                final Potion potion = potionEffects[this.index].getPotion();
                return String.valueOf(potion.isBadEffect());
            }
            return "false";
        }
    }

    public static class Icon extends TagPlayerPotion {
        private final boolean large;

        public Icon(final int index, final boolean large) {
            super(index);
            this.large = large;
        }

        @Override
        public String getValue() {
            updatePotionEffects();
            if (potionEffects.length > this.index) {
                final Potion potion = potionEffects[this.index].getPotion();
                if (potion.hasStatusIcon()) {
                    final InfoIcon icon = new InfoIcon("textures/gui/container/inventory.png");
                    final int i = potion.getStatusIconIndex();
                    if (this.large) {
                        icon.setDisplayDimensions(1, -5, 18, 18);
                    } else {
                        icon.setDisplayDimensions(1, -1, 18 / 2, 18 / 2);
                    }
                    icon.setTextureData((i % 8) * 18, 198 + (i / 8) * 18, 18, 18, 256, 256);
                    info.add(icon);
                    return getIconTag(icon);
                }
            }
            return "";
        }
    }

    public static void register() {
        for (int i = 0; i < MAXIMUM_INDEX; i++) {
            TagRegistry.INSTANCE.register(new Effect(i).setName("potioneffect"));
            TagRegistry.INSTANCE.register(new Duration(i).setName("potionduration"));
            TagRegistry.INSTANCE.register(new DurationTicks(i).setName("potiondurationticks"));
            TagRegistry.INSTANCE.register(new Negative(i).setName("potionnegative"));
            TagRegistry.INSTANCE.register(new Icon(i, false).setName("potionicon"));
            TagRegistry.INSTANCE.register(new Icon(i, true).setName("potionlargeicon"));
        }
    }

    public static void releaseResources() {
        potionEffects = null;
    }
}
