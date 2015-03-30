package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;

import java.util.Locale;

public abstract class TagPlayerGeneral extends Tag {
    @Override
    public String getCategory() {
        return "playergeneral";
    }

    public static class Light extends TagPlayerGeneral {
        @Override
        public String getValue() {
            try {
                return String.valueOf(world.getChunkFromBlockCoords(playerPosition.x, playerPosition.z).getBlockLightValue(playerPosition.x & 15, playerPosition.y, playerPosition.z & 15, world.calculateSkylightSubtracted(1.0f)));
            } catch (Exception e) {
                return "0";
            }
        }
    }

    public static class LightFeet extends TagPlayerGeneral {
        @Override
        public String getValue() {
            try {
                return String.valueOf(world.getChunkFromBlockCoords(playerPosition.x, playerPosition.z).getBlockLightValue(playerPosition.x & 15, (int) Math.round(player.boundingBox.minY), playerPosition.z & 15, world.calculateSkylightSubtracted(1.0f)));
            } catch (Exception e) {
                return "0";
            }
        }
    }

    public static class LightNoSun extends TagPlayerGeneral {
        @Override
        public String getValue() {
            try {
                return String.valueOf(world.getSavedLightValue(EnumSkyBlock.Block, playerPosition.x, playerPosition.y, playerPosition.z));
            } catch (Exception e) {
                return "0";
            }
        }
    }

    public static class LightNoSunFeet extends TagPlayerGeneral {
        @Override
        public String getValue() {
            try {
                return String.valueOf(world.getSavedLightValue(EnumSkyBlock.Block, playerPosition.x, (int) Math.round(player.boundingBox.minY), playerPosition.z));
            } catch (Exception e) {
                return "0";
            }
        }
    }

    public static class LightSun extends TagPlayerGeneral {
        @Override
        public String getValue() {
            try {
                final int lightValue = world.getSavedLightValue(EnumSkyBlock.Sky, playerPosition.x, playerPosition.y, playerPosition.z);
                final int subtract = world.calculateSkylightSubtracted(1.0f);
                return String.valueOf(MathHelper.clamp_int(lightValue - subtract, 0, 15));
            } catch (Exception e) {
                return "0";
            }
        }
    }

    public static class LightSunFeet extends TagPlayerGeneral {
        @Override
        public String getValue() {
            try {
                final int lightValue = world.getSavedLightValue(EnumSkyBlock.Sky, playerPosition.x, (int) Math.round(player.boundingBox.minY), playerPosition.z);
                final int subtract = world.calculateSkylightSubtracted(1.0f);
                return String.valueOf(MathHelper.clamp_int(lightValue - subtract, 0, 15));
            } catch (Exception e) {
                return "0";
            }
        }
    }

    public static class Score extends TagPlayerGeneral {
        @Override
        public String getValue() {
            try {
                return String.valueOf(player.getScore());
            } catch (Exception var12) {
                return "0";
            }
        }
    }

    public static class GameMode extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return I18n.format("selectWorld.gameMode." + minecraft.playerController.currentGameType.getName());
        }
    }

    public static class GameModeId extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(minecraft.playerController.currentGameType.getID());
        }
    }

    public static class Health extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.2f", player.getHealth());
        }
    }

    public static class MaxHealth extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.2f", player.getMaxHealth());
        }
    }

    public static class Armor extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.getTotalArmorValue());
        }
    }

    public static class FoodLevel extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.getFoodStats().getFoodLevel());
        }
    }

    public static class MaxFoodLevel extends TagPlayerGeneral {
        @Override
        public String getValue() {
            // TODO: use Forge method when it's in
            return String.valueOf(20);
        }
    }

    public static class Saturation extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.2f", player.getFoodStats().getSaturationLevel());
        }
    }

    public static class Exhaustion extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.2f", player.getFoodStats().foodExhaustionLevel);
        }
    }

    public static class AirTicks extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.getAir());
        }
    }

    public static class MaxAirTicks extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(300);
        }
    }

    public static class PlayerLevel extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.experienceLevel);
        }
    }

    public static class CurrentExperience extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf((int) Math.ceil(player.experience * player.xpBarCap()));
        }
    }

    public static class ExperienceUntilNext extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf((int) Math.floor((1.0 - player.experience) * player.xpBarCap()));
        }
    }

    public static class ExperienceCap extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.xpBarCap());
        }
    }

    public static class Username extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return player.getGameProfile().getName();
        }
    }

    public static class InWater extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isInWater());
        }
    }

    public static class Wet extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isWet());
        }
    }

    public static class Alive extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isEntityAlive());
        }
    }

    public static class Burning extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isBurning());
        }
    }

    public static class Riding extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isRiding());
        }
    }

    public static class Sneaking extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isSneaking());
        }
    }

    public static class Sprinting extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isSprinting());
        }
    }

    public static class Invisible extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isInvisible());
        }
    }

    public static class Eating extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isEating());
        }
    }

    public static class Invulnerable extends TagPlayerGeneral {
        @Override
        public String getValue() {
            return String.valueOf(player.isEntityInvulnerable());
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new Light().setName("light"));
        TagRegistry.INSTANCE.register(new LightFeet().setName("lightfeet"));
        TagRegistry.INSTANCE.register(new LightNoSun().setName("lightnosun"));
        TagRegistry.INSTANCE.register(new LightNoSunFeet().setName("lightnosunfeet"));
        TagRegistry.INSTANCE.register(new LightSun().setName("lightsun"));
        TagRegistry.INSTANCE.register(new LightSunFeet().setName("lightsunfeet"));
        TagRegistry.INSTANCE.register(new Score().setName("score"));
        TagRegistry.INSTANCE.register(new GameMode().setName("gamemode"));
        TagRegistry.INSTANCE.register(new GameModeId().setName("gamemodeid"));
        TagRegistry.INSTANCE.register(new Health().setName("health").setAliases("healthpoints"));
        TagRegistry.INSTANCE.register(new MaxHealth().setName("maxhealth").setAliases("maxhealthpoints"));
        TagRegistry.INSTANCE.register(new Armor().setName("armor").setAliases("armorpoints"));
        TagRegistry.INSTANCE.register(new FoodLevel().setName("foodlevel").setAliases("foodpoints"));
        TagRegistry.INSTANCE.register(new MaxFoodLevel().setName("maxfoodlevel").setAliases("maxfoodpoints"));
        TagRegistry.INSTANCE.register(new Saturation().setName("saturation").setAliases("foodsaturation"));
        TagRegistry.INSTANCE.register(new Exhaustion().setName("exhaustion").setAliases("foodexhaustion"));
        TagRegistry.INSTANCE.register(new AirTicks().setName("airticks"));
        TagRegistry.INSTANCE.register(new MaxAirTicks().setName("maxairticks"));
        TagRegistry.INSTANCE.register(new PlayerLevel().setName("playerlevel"));
        TagRegistry.INSTANCE.register(new CurrentExperience().setName("xpthislevel"));
        TagRegistry.INSTANCE.register(new ExperienceUntilNext().setName("xpuntilnext"));
        TagRegistry.INSTANCE.register(new ExperienceCap().setName("xpcap"));
        TagRegistry.INSTANCE.register(new Username().setName("username"));
        TagRegistry.INSTANCE.register(new InWater().setName("inwater").setAliases("underwater"));
        TagRegistry.INSTANCE.register(new Wet().setName("wet"));
        TagRegistry.INSTANCE.register(new Alive().setName("alive"));
        TagRegistry.INSTANCE.register(new Burning().setName("burning"));
        TagRegistry.INSTANCE.register(new Riding().setName("riding"));
        TagRegistry.INSTANCE.register(new Sneaking().setName("sneaking"));
        TagRegistry.INSTANCE.register(new Sprinting().setName("sprinting"));
        TagRegistry.INSTANCE.register(new Invisible().setName("invisible"));
        TagRegistry.INSTANCE.register(new Eating().setName("eating"));
        TagRegistry.INSTANCE.register(new Invulnerable().setName("invulnerable"));
    }
}
