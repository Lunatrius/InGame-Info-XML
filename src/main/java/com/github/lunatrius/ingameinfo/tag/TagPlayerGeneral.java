package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.EnumSkyBlock;

import java.util.Locale;

public abstract class TagPlayerGeneral extends Tag {
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
				return String.valueOf(world.getChunkFromBlockCoords(playerPosition.x, playerPosition.z).getSavedLightValue(EnumSkyBlock.Block, playerPosition.x & 15, playerPosition.y, playerPosition.z & 15));
			} catch (Exception e) {
				return "0";
			}
		}
	}

	public static class LightNoSunFeet extends TagPlayerGeneral {
		@Override
		public String getValue() {
			try {
				return String.valueOf(world.getChunkFromBlockCoords(playerPosition.x, playerPosition.z).getSavedLightValue(EnumSkyBlock.Block, playerPosition.x & 15, (int) Math.round(player.boundingBox.minY), playerPosition.z & 15));
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

	public static class Saturation extends TagPlayerGeneral {
		@Override
		public String getValue() {
			return String.format(Locale.ENGLISH, "%.2f", player.getFoodStats().getSaturationLevel());
		}
	}

	public static class AirTicks extends TagPlayerGeneral {
		@Override
		public String getValue() {
			return String.valueOf(player.getAir());
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
		TagRegistry.INSTANCE.register(new Light(), "light");
		TagRegistry.INSTANCE.register(new LightFeet(), "lightfeet");
		TagRegistry.INSTANCE.register(new LightNoSun(), "lightnosun");
		TagRegistry.INSTANCE.register(new LightNoSunFeet(), "lightnosunfeet");
		TagRegistry.INSTANCE.register(new Score(), "score");
		TagRegistry.INSTANCE.register(new GameMode(), "gamemode");
		TagRegistry.INSTANCE.register(new GameModeId(), "gamemodeid");
		TagRegistry.INSTANCE.register(new Health(), "health", "healthpoints");
		TagRegistry.INSTANCE.register(new MaxHealth(), "maxhealth");
		TagRegistry.INSTANCE.register(new Armor(), "armor", "armorpoints");
		TagRegistry.INSTANCE.register(new FoodLevel(), "foodlevel", "foodpoints");
		TagRegistry.INSTANCE.register(new Saturation(), "saturation", "foodsaturation");
		TagRegistry.INSTANCE.register(new AirTicks(), "airticks");
		TagRegistry.INSTANCE.register(new PlayerLevel(), "playerlevel");
		TagRegistry.INSTANCE.register(new CurrentExperience(), "xpthislevel");
		TagRegistry.INSTANCE.register(new ExperienceUntilNext(), "xpuntilnext");
		TagRegistry.INSTANCE.register(new ExperienceCap(), "xpcap");
		TagRegistry.INSTANCE.register(new Username(), "username");
		TagRegistry.INSTANCE.register(new InWater(), "inwater", "underwater");
		TagRegistry.INSTANCE.register(new Wet(), "wet");
		TagRegistry.INSTANCE.register(new Alive(), "alive");
		TagRegistry.INSTANCE.register(new Burning(), "burning");
		TagRegistry.INSTANCE.register(new Riding(), "riding");
		TagRegistry.INSTANCE.register(new Sneaking(), "sneaking");
		TagRegistry.INSTANCE.register(new Sprinting(), "sprinting");
		TagRegistry.INSTANCE.register(new Invisible(), "invisible");
		TagRegistry.INSTANCE.register(new Eating(), "eating");
		TagRegistry.INSTANCE.register(new Invulnerable(), "invulnerable");
	}
}
