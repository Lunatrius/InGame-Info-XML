package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.client.gui.overlay.InfoIcon;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.ResourcePackRepository;

import java.util.List;

public abstract class TagMisc extends Tag {
    protected static final ResourcePackRepository resourcePackRepository = minecraft.getResourcePackRepository();

    @Override
    public String getCategory() {
        return "misc";
    }

    public static class MemoryMaximum extends TagMisc {
        @Override
        public String getValue() {
            return String.valueOf(Runtime.getRuntime().maxMemory());
        }
    }

    public static class MemoryTotal extends TagMisc {
        @Override
        public String getValue() {
            return String.valueOf(Runtime.getRuntime().totalMemory());
        }
    }

    public static class MemoryFree extends TagMisc {
        @Override
        public String getValue() {
            return String.valueOf(Runtime.getRuntime().freeMemory());
        }
    }

    public static class MemoryUsed extends TagMisc {
        @Override
        public String getValue() {
            return String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        }
    }

    public static class FPS extends TagMisc {
        @Override
        public String getValue() {
            return String.valueOf(Minecraft.getDebugFPS());
        }
    }

    public static class ResourcePack extends TagMisc {
        @Override
        public String getValue() {
            final List<ResourcePackRepository.Entry> repositoryEntries = resourcePackRepository.getRepositoryEntries();
            if (repositoryEntries.size() > 0) {
                return repositoryEntries.get(0).getResourcePackName();
            }
            return resourcePackRepository.rprDefaultResourcePack.getPackName();
        }
    }

    public static class EntitiesRendered extends TagMisc {
        @Override
        public String getValue() {
            return String.valueOf(minecraft.renderGlobal.countEntitiesRendered);
        }
    }

    public static class EntitiesTotal extends TagMisc {
        @Override
        public String getValue() {
            return String.valueOf(minecraft.renderGlobal.countEntitiesTotal);
        }
    }

    public static class Server extends TagMisc {
        @Override
        public String getValue() {
            final String str = player.connection.getNetworkManager().getRemoteAddress().toString();
            final int i = str.indexOf("/");
            final int j = str.indexOf(":");
            if (i < 0) {
                return "localhost";
            }

            final String name = (i == 0) ? str.substring(i + 1, j) : str.substring(0, i);
            final String port = str.substring(j + 1);
            return name + (port.equals("25565") ? "" : ":" + port);
        }
    }

    public static class ServerName extends TagMisc {
        @Override
        public String getValue() {
            final String str = player.connection.getNetworkManager().getRemoteAddress().toString();
            final int i = str.indexOf("/");
            if (i < 0) {
                return "localhost";
            } else if (i == 0) {
                return str.substring(i + 1, str.indexOf(":"));
            }
            return str.substring(0, i);
        }
    }

    public static class ServerIP extends TagMisc {
        @Override
        public String getValue() {
            final String str = player.connection.getNetworkManager().getRemoteAddress().toString();
            final int i = str.indexOf("/");
            if (i < 0) {
                return "127.0.0.1";
            }
            return str.substring(i + 1, str.indexOf(":"));
        }
    }

    public static class ServerPort extends TagMisc {
        @Override
        public String getValue() {
            final String str = player.connection.getNetworkManager().getRemoteAddress().toString();
            final int i = str.indexOf("/");
            if (i < 0) {
                return "-1";
            }
            return str.substring(str.indexOf(":") + 1);
        }
    }

    public static class Ping extends TagMisc {
        @Override
        public String getValue() {
            try {
                final NetworkPlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(player.getUniqueID());
                return String.valueOf(playerInfo.getResponseTime());
            } catch (final Exception e) {
            }
            return "-1";
        }
    }

    public static class PingIcon extends TagMisc {
        @Override
        public String getValue() {
            try {
                final NetworkPlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(player.getUniqueID());
                final int responseTime = playerInfo.getResponseTime();
                int pingIndex = 4;
                if (responseTime < 0) {
                    pingIndex = 5;
                } else if (responseTime < 150) {
                    pingIndex = 0;
                } else if (responseTime < 300) {
                    pingIndex = 1;
                } else if (responseTime < 600) {
                    pingIndex = 2;
                } else if (responseTime < 1000) {
                    pingIndex = 3;
                }

                final InfoIcon icon = new InfoIcon("textures/gui/icons.png");
                icon.setDisplayDimensions(0, 0, 10, 8);
                icon.setTextureData(0, 176 + pingIndex * 8, 10, 8, 256, 256);
                info.add(icon);
                return getIconTag(icon);
            } catch (final Exception e) {
            }
            return "-1";
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new MemoryMaximum().setName("memmax"));
        TagRegistry.INSTANCE.register(new MemoryTotal().setName("memtotal"));
        TagRegistry.INSTANCE.register(new MemoryFree().setName("memfree"));
        TagRegistry.INSTANCE.register(new MemoryUsed().setName("memused"));
        TagRegistry.INSTANCE.register(new FPS().setName("fps"));
        TagRegistry.INSTANCE.register(new ResourcePack().setName("resourcepack"));
        TagRegistry.INSTANCE.register(new EntitiesRendered().setName("entitiesrendered"));
        TagRegistry.INSTANCE.register(new EntitiesTotal().setName("entitiestotal"));
        TagRegistry.INSTANCE.register(new Server().setName("server"));
        TagRegistry.INSTANCE.register(new ServerName().setName("servername"));
        TagRegistry.INSTANCE.register(new ServerIP().setName("serverip"));
        TagRegistry.INSTANCE.register(new ServerPort().setName("serverport"));
        TagRegistry.INSTANCE.register(new Ping().setName("ping"));
        TagRegistry.INSTANCE.register(new PingIcon().setName("pingicon"));
    }
}
