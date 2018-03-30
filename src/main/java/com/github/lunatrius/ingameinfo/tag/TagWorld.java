package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.core.world.chunk.ChunkHelper;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Biomes;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;

import java.util.Locale;

public abstract class TagWorld extends Tag {
    @Override
    public String getCategory() {
        return "world";
    }

    public static class Name extends TagWorld {
        @Override
        public String getValue() {
            if (server != null) {
                final WorldServer worldServer = DimensionManager.getWorld(player.dimension);
                if (worldServer != null) {
                    return worldServer.getWorldInfo().getWorldName();
                }
            }
            return world.getWorldInfo().getWorldName();
        }
    }

    public static class Size extends TagWorld {
        @Override
        public String getValue() {
            if (server != null) {
                final WorldServer worldServer = DimensionManager.getWorld(player.dimension);
                if (worldServer != null) {
                    return String.valueOf(worldServer.getWorldInfo().getSizeOnDisk());
                }
            }
            return String.valueOf(world.getWorldInfo().getSizeOnDisk());
        }
    }

    public static class SizeMB extends TagWorld {
        @Override
        public String getValue() {
            if (server != null) {
                final WorldServer worldServer = DimensionManager.getWorld(player.dimension);
                if (worldServer != null) {
                    return String.format(Locale.ENGLISH, "%.1f", worldServer.getWorldInfo().getSizeOnDisk() / 1048576.0);
                }
            }
            return String.format(Locale.ENGLISH, "%.1f", world.getWorldInfo().getSizeOnDisk() / 1048576.0);
        }
    }

    public static class Seed extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(seed);
        }
    }

    public static class Difficulty extends TagWorld {
        @Override
        public String getValue() {
            if (server != null) {
                final WorldServer worldServer = DimensionManager.getWorld(player.dimension);
                if (worldServer != null) {
                    return I18n.format(worldServer.getDifficulty().getDifficultyResourceKey());
                }
            }
            return I18n.format(minecraft.gameSettings.difficulty.getDifficultyResourceKey());
        }
    }

    public static class DifficultyId extends TagWorld {
        @Override
        public String getValue() {
            if (server != null) {
                final WorldServer worldServer = DimensionManager.getWorld(player.dimension);
                if (worldServer != null) {
                    return String.valueOf(worldServer.getDifficulty().getDifficultyId());
                }
            }
            return String.valueOf(minecraft.gameSettings.difficulty.getDifficultyId());
        }
    }

    public static class LocalDifficulty extends TagWorld {
        @Override
        public String getValue() {
            DifficultyInstance difficulty = world.getDifficultyForLocation(playerPosition);
            if (server != null) {
                final WorldServer worldServer = DimensionManager.getWorld(player.dimension);
                if (worldServer != null) {
                    difficulty = worldServer.getDifficultyForLocation(playerPosition);
                }
            }
            return String.format(Locale.ENGLISH, "%.2f", difficulty.getAdditionalDifficulty());
        }
    }

    public static class Dimension extends TagWorld {
        @Override
        public String getValue() {
            return DimensionType.getById(player.dimension).getName();
        }
    }

    public static class DimensionId extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(player.dimension);
        }
    }

    public static class Biome extends TagWorld {
        @Override
        public String getValue() {
            return world.getBiome(playerPosition).getBiomeName();
        }
    }

    public static class BiomeId extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(net.minecraft.world.biome.Biome.getIdForBiome(world.getBiome(playerPosition)));
        }
    }

    public static class Daytime extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(world.calculateSkylightSubtracted(1.0f) < 4);
        }
    }

    public static class MoonPhase extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(world.getMoonPhase());
        }
    }

    public static class Raining extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(world.isRaining() && world.getBiome(playerPosition).canRain());
        }
    }

    public static class Thundering extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(world.isThundering() && world.getBiome(playerPosition).canRain());
        }
    }

    public static class Snowing extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(world.isRaining() && world.getBiome(playerPosition).getEnableSnow());
        }
    }

    public static class NextWeatherChange extends TagWorld {
        @Override
        public String getValue() {
            if (server == null) {
                return "?";
            }

            final WorldInfo worldInfo = server.getWorld(0).getWorldInfo();
            final int clearTime = worldInfo.getCleanWeatherTime();
            final float seconds = (clearTime > 0 ? clearTime : worldInfo.getRainTime()) / 20f;
            if (seconds < 60) {
                return String.format(Locale.ENGLISH, "%.1fs", seconds);
            } else if (seconds < 3600) {
                return String.format(Locale.ENGLISH, "%.1fm", seconds / 60);
            }
            return String.format(Locale.ENGLISH, "%.1fh", seconds / 3600);
        }
    }

    public static class Slimes extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(hasSeed && ChunkHelper.isSlimeChunk(seed, playerPosition) || world.getBiome(playerPosition) == Biomes.SWAMPLAND && playerPosition.y > 50 && playerPosition.y < 70);
        }
    }

    public static class SlimeChunk extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(hasSeed && ChunkHelper.isSlimeChunk(seed, playerPosition));
        }
    }

    public static class Hardcore extends TagWorld {
        @Override
        public String getValue() {
            return String.valueOf(world.getWorldInfo().isHardcoreModeEnabled());
        }
    }

    public static class Temperature extends TagWorld {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.0f", world.getBiome(playerPosition).getDefaultTemperature() * 100);
        }
    }

    public static class LocalTemperature extends TagWorld {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.2f", world.getBiome(playerPosition).getTemperature(playerPosition) * 100);
        }
    }

    public static class Humidity extends TagWorld {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%.0f", world.getBiome(playerPosition).getRainfall() * 100);
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new Name().setName("worldname"));
        TagRegistry.INSTANCE.register(new Size().setName("worldsize"));
        TagRegistry.INSTANCE.register(new SizeMB().setName("worldsizemb"));
        TagRegistry.INSTANCE.register(new Seed().setName("seed"));
        TagRegistry.INSTANCE.register(new Difficulty().setName("difficulty"));
        TagRegistry.INSTANCE.register(new DifficultyId().setName("difficultyid"));
        TagRegistry.INSTANCE.register(new LocalDifficulty().setName("localdifficulty"));
        TagRegistry.INSTANCE.register(new Dimension().setName("dimension"));
        TagRegistry.INSTANCE.register(new DimensionId().setName("dimensionid"));
        TagRegistry.INSTANCE.register(new Biome().setName("biome"));
        TagRegistry.INSTANCE.register(new BiomeId().setName("biomeid"));
        TagRegistry.INSTANCE.register(new Daytime().setName("daytime"));
        TagRegistry.INSTANCE.register(new MoonPhase().setName("moonphase"));
        TagRegistry.INSTANCE.register(new Raining().setName("raining"));
        TagRegistry.INSTANCE.register(new Thundering().setName("thundering"));
        TagRegistry.INSTANCE.register(new Snowing().setName("snowing"));
        TagRegistry.INSTANCE.register(new NextWeatherChange().setName("nextweatherchange").setAliases("nextrain"));
        TagRegistry.INSTANCE.register(new Slimes().setName("slimes"));
        TagRegistry.INSTANCE.register(new SlimeChunk().setName("slimechunk"));
        TagRegistry.INSTANCE.register(new Hardcore().setName("hardcore"));
        TagRegistry.INSTANCE.register(new Temperature().setName("temperature"));
        TagRegistry.INSTANCE.register(new LocalTemperature().setName("localtemperature"));
        TagRegistry.INSTANCE.register(new Humidity().setName("humidity"));
    }
}
