package com.github.lunatrius.ingameinfo.integration.thaumcraft.tag;

import com.github.lunatrius.ingameinfo.tag.TagIntegration;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import thaumcraft.common.Thaumcraft;

public abstract class TagThaumcraft extends TagIntegration {
    @Override
    public String getCategory() {
        return "thaumcraft";
    }

    public static class WarpPerm extends TagThaumcraft {
        @Override
        public String getValue() {
            try {
                return String.valueOf(Thaumcraft.proxy.getPlayerKnowledge().getWarpPerm(player.getCommandSenderName()));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class WarpSticky extends TagThaumcraft {
        @Override
        public String getValue() {
            try {
                return String.valueOf(Thaumcraft.proxy.getPlayerKnowledge().getWarpSticky(player.getCommandSenderName()));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class WarpTemp extends TagThaumcraft {
        @Override
        public String getValue() {
            try {
                return String.valueOf(Thaumcraft.proxy.getPlayerKnowledge().getWarpTemp(player.getCommandSenderName()));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class WarpTotal extends TagThaumcraft {
        @Override
        public String getValue() {
            try {
                return String.valueOf(Thaumcraft.proxy.getPlayerKnowledge().getWarpTotal(player.getCommandSenderName()));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new WarpPerm().setName("tcwarpperm"));
        TagRegistry.INSTANCE.register(new WarpSticky().setName("tcwarpsticky"));
        TagRegistry.INSTANCE.register(new WarpTemp().setName("tcwarptemp"));
        TagRegistry.INSTANCE.register(new WarpTotal().setName("tcwarptotal"));
    }
}
