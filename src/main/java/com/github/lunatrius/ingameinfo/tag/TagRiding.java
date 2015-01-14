package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;

public abstract class TagRiding extends Tag {
    @Override
    public String getCategory() {
        return "riding";
    }

    public static class IsHorse extends TagRiding {
        @Override
        public String getValue() {
            return String.valueOf(player.ridingEntity instanceof EntityHorse);
        }
    }

    public static class HorseHealth extends TagRiding {
        @Override
        public String getValue() {
            if (player.ridingEntity instanceof EntityHorse) {
                return String.valueOf(((EntityHorse) player.ridingEntity).getHealth());
            }
            return "-1";
        }
    }

    public static class HorseMaxHealth extends TagRiding {
        @Override
        public String getValue() {
            if (player.ridingEntity instanceof EntityHorse) {
                return String.valueOf(((EntityHorse) player.ridingEntity).getMaxHealth());
            }
            return "-1";
        }
    }

    public static class HorseSpeed extends TagRiding {
        @Override
        public String getValue() {
            if (player.ridingEntity instanceof EntityHorse) {
                return String.format("%.6f", ((EntityHorse) player.ridingEntity).getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            }
            return "-1";
        }
    }

    public static class HorseJump extends TagRiding {
        @Override
        public String getValue() {
            if (player.ridingEntity instanceof EntityHorse) {
                return String.format("%.6f", ((EntityHorse) player.ridingEntity).getHorseJumpStrength());
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
