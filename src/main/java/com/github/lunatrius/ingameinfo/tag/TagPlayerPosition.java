package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.util.MathHelper;

import java.util.Locale;

public abstract class TagPlayerPosition extends Tag {
	protected static final String[] ROUGHDIRECTION = {
			"South", "West", "North", "East"
	};
	protected static final String[] FINEDIRECTION = {
			"South", "South West", "West", "North West", "North", "North East", "East", "South East"
	};
	protected static final String[] ABRROUGHDIRECTION = {
			"S", "W", "N", "E"
	};
	protected static final String[] ABRFINEDIRECTION = {
			"S", "SW", "W", "NW", "N", "NE", "E", "SE"
	};

	public static class ChunkX extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.valueOf(playerPosition.x >> 4);
		}
	}

	public static class ChunkZ extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.valueOf(playerPosition.z >> 4);
		}
	}

	public static class ChunkOffsetX extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.valueOf(playerPosition.x & 0x0F);
		}
	}

	public static class ChunkOffsetZ extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.valueOf(playerPosition.z & 0x0F);
		}
	}

	public static class X extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format(Locale.ENGLISH, "%.2f", player.posX);
		}
	}

	public static class Y extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format(Locale.ENGLISH, "%.2f", player.posY);
		}
	}

	public static class YFeet extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format(Locale.ENGLISH, "%.2f", player.boundingBox.minY);
		}
	}

	public static class Z extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format(Locale.ENGLISH, "%.2f", player.posZ);
		}
	}

	public static class Xi extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.valueOf(playerPosition.x);
		}
	}

	public static class Yi extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.valueOf(playerPosition.y);
		}
	}

	public static class YFeeti extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.valueOf((int) Math.floor(player.boundingBox.minY));
		}
	}

	public static class Zi extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.valueOf(playerPosition.z);
		}
	}

	public static class Speed extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format("%.2f", Math.sqrt(playerMotion.x * playerMotion.x + playerMotion.y * playerMotion.y + playerMotion.z * playerMotion.z));
		}
	}

	public static class SpeedX extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format("%.2f", Math.abs(playerMotion.x));
		}
	}

	public static class SpeedY extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format("%.2f", Math.abs(playerMotion.y));
		}
	}

	public static class SpeedZ extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format("%.2f", Math.abs(playerMotion.z));
		}
	}

	public static class SpeedXZ extends TagPlayerPosition {
		@Override
		public String getValue() {
			return String.format("%.2f", Math.sqrt(playerMotion.x * playerMotion.x + playerMotion.z * playerMotion.z));
		}
	}

	public static class Direction extends TagPlayerPosition {
		@Override
		public String getValue() {
			float direction = player.rotationYaw % 360;
			if (direction >= 180) {
				direction -= 360;
			} else if (direction < -180) {
				direction += 360;
			}
			return String.format("%.2f", direction);
		}
	}

	public static class RoughDirection extends TagPlayerPosition {
		@Override
		public String getValue() {
			return ROUGHDIRECTION[MathHelper.floor_double(player.rotationYaw * 4.0 / 360.0 + 0.5) & 3];
		}
	}

	public static class FineDirection extends TagPlayerPosition {
		@Override
		public String getValue() {
			return FINEDIRECTION[MathHelper.floor_double(player.rotationYaw * 8.0 / 360.0 + 0.5) & 7];
		}
	}

	public static class AbbreviatedRoughDirection extends TagPlayerPosition {
		@Override
		public String getValue() {
			return ABRROUGHDIRECTION[MathHelper.floor_double(player.rotationYaw * 4.0 / 360.0 + 0.5) & 3];
		}
	}

	public static class AbbreviatedFineDirection extends TagPlayerPosition {
		@Override
		public String getValue() {
			return ABRFINEDIRECTION[MathHelper.floor_double(player.rotationYaw * 8.0 / 360.0 + 0.5) & 7];
		}
	}

	public static class DirectionHud extends TagPlayerPosition {
		@Override
		public String getValue() {
			int direction = MathHelper.floor_double(player.rotationYaw * 16.0f / 360.0f + 0.5) & 15;
			String left = ABRFINEDIRECTION[(direction / 2 + ABRFINEDIRECTION.length - 1) % ABRFINEDIRECTION.length];
			String center = ABRFINEDIRECTION[(direction / 2 + ABRFINEDIRECTION.length) % ABRFINEDIRECTION.length];
			String right = ABRFINEDIRECTION[(direction / 2 + ABRFINEDIRECTION.length + 1) % ABRFINEDIRECTION.length];
			if (direction % 2 == 0) {
				return String.format("\u00a7r%s   \u00a7c%s\u00a7r   %s", left, center, right);
			}
			return String.format("\u00a7r   %2s   %2s   ", center, right);
		}
	}

	public static void register() {
		TagRegistry.INSTANCE.register(new ChunkX(), "chunkx");
		TagRegistry.INSTANCE.register(new ChunkZ(), "chunkz");
		TagRegistry.INSTANCE.register(new ChunkOffsetX(), "chunkoffsetx");
		TagRegistry.INSTANCE.register(new ChunkOffsetZ(), "chunkoffsetz");
		TagRegistry.INSTANCE.register(new X(), "x", "xr");
		TagRegistry.INSTANCE.register(new Y(), "y", "yr");
		TagRegistry.INSTANCE.register(new YFeet(), "yfeet", "yfeetr");
		TagRegistry.INSTANCE.register(new Z(), "z", "zr");
		TagRegistry.INSTANCE.register(new Xi(), "xi");
		TagRegistry.INSTANCE.register(new Yi(), "yi");
		TagRegistry.INSTANCE.register(new YFeeti(), "yfeeti");
		TagRegistry.INSTANCE.register(new Zi(), "zi");
		TagRegistry.INSTANCE.register(new Speed(), "speed");
		TagRegistry.INSTANCE.register(new SpeedX(), "speedx");
		TagRegistry.INSTANCE.register(new SpeedY(), "speedy");
		TagRegistry.INSTANCE.register(new SpeedZ(), "speedz");
		TagRegistry.INSTANCE.register(new SpeedXZ(), "speedxz");
		TagRegistry.INSTANCE.register(new Direction(), "direction");
		TagRegistry.INSTANCE.register(new RoughDirection(), "roughdirection");
		TagRegistry.INSTANCE.register(new FineDirection(), "finedirection");
		TagRegistry.INSTANCE.register(new AbbreviatedRoughDirection(), "abrroughdirection");
		TagRegistry.INSTANCE.register(new AbbreviatedFineDirection(), "abrfinedirection");
		TagRegistry.INSTANCE.register(new DirectionHud(), "directionhud");
	}
}
