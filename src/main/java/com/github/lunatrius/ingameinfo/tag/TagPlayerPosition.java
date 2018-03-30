package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;

public abstract class TagPlayerPosition extends Tag {
    private static final int TICKS = 20;

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
    protected static final String[] AXISDIRECTION = {
            "+Z", "-X", "-Z", "+X"
    };

    @Override
    public String getCategory() {
        return "playerposition";
    }

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

    public static class YEye extends TagPlayerPosition {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.2f", player.posY + player.getEyeHeight());
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

    public static class YEyei extends TagPlayerPosition {
        @Override
        public String getValue() {
            return String.valueOf(MathHelper.floor(player.posY + player.getEyeHeight()));
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
            return String.format(Locale.ENGLISH, "%.3f", TICKS * Math.sqrt(playerMotion.x * playerMotion.x + playerMotion.y * playerMotion.y + playerMotion.z * playerMotion.z));
        }
    }

    public static class SpeedX extends TagPlayerPosition {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.3f", TICKS * Math.abs(playerMotion.x));
        }
    }

    public static class SpeedY extends TagPlayerPosition {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.3f", TICKS * Math.abs(playerMotion.y));
        }
    }

    public static class SpeedZ extends TagPlayerPosition {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.3f", TICKS * Math.abs(playerMotion.z));
        }
    }

    public static class SpeedXZ extends TagPlayerPosition {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.3f", TICKS * Math.sqrt(playerMotion.x * playerMotion.x + playerMotion.z * playerMotion.z));
        }
    }

    public static class Pitch extends TagPlayerPosition {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.2f", player.rotationPitch);
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
            return String.format(Locale.ENGLISH, "%.2f", direction);
        }
    }

    public static class RoughDirection extends TagPlayerPosition {
        @Override
        public String getValue() {
            return ROUGHDIRECTION[MathHelper.floor(player.rotationYaw * 4.0 / 360.0 + 0.5) & 3];
        }
    }

    public static class FineDirection extends TagPlayerPosition {
        @Override
        public String getValue() {
            return FINEDIRECTION[MathHelper.floor(player.rotationYaw * 8.0 / 360.0 + 0.5) & 7];
        }
    }

    public static class AbbreviatedRoughDirection extends TagPlayerPosition {
        @Override
        public String getValue() {
            return ABRROUGHDIRECTION[MathHelper.floor(player.rotationYaw * 4.0 / 360.0 + 0.5) & 3];
        }
    }

    public static class AbbreviatedFineDirection extends TagPlayerPosition {
        @Override
        public String getValue() {
            return ABRFINEDIRECTION[MathHelper.floor(player.rotationYaw * 8.0 / 360.0 + 0.5) & 7];
        }
    }

    public static class AxisDirection extends TagPlayerPosition {
        @Override
        public String getValue() {
            return AXISDIRECTION[MathHelper.floor(player.rotationYaw * 4.0 / 360.0 + 0.5) & 3];
        }
    }

    public static class DirectionHud extends TagPlayerPosition {
        @Override
        public String getValue() {
            final int direction = MathHelper.floor(player.rotationYaw * 16.0f / 360.0f + 0.5) & 15;
            final String left = ABRFINEDIRECTION[(direction / 2 + ABRFINEDIRECTION.length - 1) % ABRFINEDIRECTION.length];
            final String center = ABRFINEDIRECTION[(direction / 2 + ABRFINEDIRECTION.length) % ABRFINEDIRECTION.length];
            final String right = ABRFINEDIRECTION[(direction / 2 + ABRFINEDIRECTION.length + 1) % ABRFINEDIRECTION.length];
            if (direction % 2 == 0) {
                return String.format("\u00a7r%s   \u00a7c%s\u00a7r   %s", left, center, right);
            }
            return String.format("\u00a7r   %2s   %2s   ", center, right);
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new ChunkX().setName("chunkx"));
        TagRegistry.INSTANCE.register(new ChunkZ().setName("chunkz"));
        TagRegistry.INSTANCE.register(new ChunkOffsetX().setName("chunkoffsetx"));
        TagRegistry.INSTANCE.register(new ChunkOffsetZ().setName("chunkoffsetz"));
        TagRegistry.INSTANCE.register(new X().setName("x").setAliases("xr"));
        TagRegistry.INSTANCE.register(new Y().setName("y").setAliases("yr"));
        TagRegistry.INSTANCE.register(new YEye().setName("yeye").setAliases("yeyer"));
        TagRegistry.INSTANCE.register(new Z().setName("z").setAliases("zr"));
        TagRegistry.INSTANCE.register(new Xi().setName("xi"));
        TagRegistry.INSTANCE.register(new Yi().setName("yi"));
        TagRegistry.INSTANCE.register(new YEyei().setName("yeyei"));
        TagRegistry.INSTANCE.register(new Zi().setName("zi"));
        TagRegistry.INSTANCE.register(new Speed().setName("speed"));
        TagRegistry.INSTANCE.register(new SpeedX().setName("speedx"));
        TagRegistry.INSTANCE.register(new SpeedY().setName("speedy"));
        TagRegistry.INSTANCE.register(new SpeedZ().setName("speedz"));
        TagRegistry.INSTANCE.register(new SpeedXZ().setName("speedxz"));
        TagRegistry.INSTANCE.register(new Pitch().setName("pitch"));
        TagRegistry.INSTANCE.register(new Direction().setName("direction"));
        TagRegistry.INSTANCE.register(new RoughDirection().setName("roughdirection"));
        TagRegistry.INSTANCE.register(new FineDirection().setName("finedirection"));
        TagRegistry.INSTANCE.register(new AbbreviatedRoughDirection().setName("abrroughdirection"));
        TagRegistry.INSTANCE.register(new AbbreviatedFineDirection().setName("abrfinedirection"));
        TagRegistry.INSTANCE.register(new AxisDirection().setName("axisdirection"));
        TagRegistry.INSTANCE.register(new DirectionHud().setName("directionhud"));
    }
}
