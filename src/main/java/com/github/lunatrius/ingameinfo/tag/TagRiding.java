package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.AbstractHorse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class TagRiding extends Tag {
    private static final int TICKS = 20;
    private static final double CONSTANT = 2.15858575199013618; // playerSpeed / internalPlayerSpeed (0.1)

    @Override
    public String getCategory() {
        return "riding";
    }

    public static class IsHorse extends TagRiding {
        @Override
        public String getValue() {
            return String.valueOf(player.getRidingEntity() instanceof AbstractHorse);
        }
    }

    public static class HorseHealth extends TagRiding {
        @Override
        public String getValue() {
            final Entity ridingEntity = player.getRidingEntity();
            if (ridingEntity instanceof AbstractHorse) {
                return String.valueOf(((AbstractHorse) ridingEntity).getHealth());
            }
            return "-1";
        }
    }

    public static class HorseMaxHealth extends TagRiding {
        @Override
        public String getValue() {
            final Entity ridingEntity = player.getRidingEntity();
            if (ridingEntity instanceof AbstractHorse) {
                return String.valueOf(((AbstractHorse) ridingEntity).getMaxHealth());
            }
            return "-1";
        }
    }

    public static class HorseSpeed extends TagRiding {
        @Override
        public String getValue() {
            final Entity ridingEntity = player.getRidingEntity();
            if (ridingEntity instanceof AbstractHorse) {
                return String.format(Locale.ENGLISH, "%.3f", TICKS * CONSTANT * ((AbstractHorse) ridingEntity).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            }
            return "-1";
        }
    }

    public static class HorseJump extends TagRiding {
        private final Map<Double, Double> jumpHeightCache = new HashMap<>();

        private double getJumpHeight(final AbstractHorse horse) {
            final double jumpStrength = horse.getHorseJumpStrength();

            final Double height = this.jumpHeightCache.get(jumpStrength);
            if (height != null) {
                return height;
            }

            double jumpHeight = 0;
            double velocity = jumpStrength;
            while (velocity > 0) {
                jumpHeight += velocity;
                velocity -= 0.08;
                velocity *= 0.98;
            }

            if (this.jumpHeightCache.size() > 16) {
                Reference.logger.trace("Clearing horse jump height cache.");
                this.jumpHeightCache.clear();
            }

            this.jumpHeightCache.put(jumpStrength, jumpHeight);
            return jumpHeight;
        }

        @Override
        public String getValue() {
            final Entity ridingEntity = player.getRidingEntity();
            if (ridingEntity instanceof AbstractHorse) {
                return String.format(Locale.ENGLISH, "%.3f", getJumpHeight((AbstractHorse) ridingEntity));
            }
            return "-1";
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new IsHorse().setName("ridinghorse"));
        TagRegistry.INSTANCE.register(new HorseHealth().setName("horsehealth"));
        TagRegistry.INSTANCE.register(new HorseMaxHealth().setName("horsemaxhealth"));
        TagRegistry.INSTANCE.register(new HorseSpeed().setName("horsespeed"));
        TagRegistry.INSTANCE.register(new HorseJump().setName("horsejumpstrength").setAliases("horsejumpstr"));
    }
}
