package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class TagTime extends Tag {
    @Override
    public String getCategory() {
        return "time";
    }

    public static class MinecraftDay extends TagTime {
        @Override
        public String getValue() {
            return String.format(Locale.ENGLISH, "%d", world.getWorldTime() / 24000);
        }
    }

    public static class MinecraftHour extends TagTime {
        @Override
        public String getValue() {
            final long hour = (world.getWorldTime() / 1000 + 6) % 24;
            return String.format(Locale.ENGLISH, "%02d", hour);
        }
    }

    public static class MinecraftMinute extends TagTime {
        @Override
        public String getValue() {
            final long minute = (world.getWorldTime() % 1000) * 60 / 1000;
            return String.format(Locale.ENGLISH, "%02d", minute);
        }
    }

    public static class Minecraft24 extends TagTime {
        @Override
        public String getValue() {
            final long time = world.getWorldTime();
            final long hour = (time / 1000 + 6) % 24;
            final long minute = (time % 1000) * 60 / 1000;
            return String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);
        }
    }

    public static class Minecraft12 extends TagTime {
        @Override
        public String getValue() {
            final long time = world.getWorldTime();
            long hour = (time / 1000 + 6) % 24;
            final long minute = (time % 1000) * 60 / 1000;
            String ampm = "AM";
            if (hour >= 12) {
                hour -= 12;
                ampm = "PM";
            }
            if (hour == 0) {
                hour += 12;
            }
            return String.format(Locale.ENGLISH, "%02d:%02d %s", hour, minute, ampm);
        }
    }

    public static class Real24 extends TagTime {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        @Override
        public String getValue() {
            return this.dateFormat.format(new Date());
        }
    }

    public static class Real12 extends TagTime {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

        @Override
        public String getValue() {
            return this.dateFormat.format(new Date());
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new MinecraftDay().setName("day"));
        TagRegistry.INSTANCE.register(new MinecraftHour().setName("mctimeh"));
        TagRegistry.INSTANCE.register(new MinecraftMinute().setName("mctimem"));
        TagRegistry.INSTANCE.register(new Minecraft24().setName("mctime24").setAliases("mctime"));
        TagRegistry.INSTANCE.register(new Minecraft12().setName("mctime12"));
        TagRegistry.INSTANCE.register(new Real24().setName("rltime24").setAliases("rltime"));
        TagRegistry.INSTANCE.register(new Real12().setName("rltime12"));
    }
}
