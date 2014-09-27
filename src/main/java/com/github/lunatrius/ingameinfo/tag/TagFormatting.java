package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;

public abstract class TagFormatting extends Tag {
    private static final String SIGN = "\u00a7";

    @Override
    public String getCategory() {
        return "formatting";
    }

    public static class Black extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "0";
        }
    }

    public static class DarkBlue extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "1";
        }
    }

    public static class DarkGreen extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "2";
        }
    }

    public static class DarkAqua extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "3";
        }
    }

    public static class DarkRed extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "4";
        }
    }

    public static class DarkPurple extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "5";
        }
    }

    public static class Gold extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "6";
        }
    }

    public static class Gray extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "7";
        }
    }

    public static class DarkGray extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "8";
        }
    }

    public static class Blue extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "9";
        }
    }

    public static class Green extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "a";
        }
    }

    public static class Aqua extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "b";
        }
    }

    public static class Red extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "c";
        }
    }

    public static class LightPurple extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "d";
        }
    }

    public static class Yellow extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "e";
        }
    }

    public static class White extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "f";
        }
    }

    public static class Obfuscated extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "k";
        }
    }

    public static class Bold extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "l";
        }
    }

    public static class Strikethrough extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "m";
        }
    }

    public static class Underline extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "n";
        }
    }

    public static class Italic extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "o";
        }
    }

    public static class Reset extends TagFormatting {
        @Override
        public String getValue() {
            return SIGN + "r";
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new Black().setName("black"));
        TagRegistry.INSTANCE.register(new DarkBlue().setName("darkblue").setAliases("navy"));
        TagRegistry.INSTANCE.register(new DarkGreen().setName("darkgreen"));
        TagRegistry.INSTANCE.register(new DarkAqua().setName("darkaqua").setAliases("darkcyan", "turquoise"));
        TagRegistry.INSTANCE.register(new DarkRed().setName("darkred"));
        TagRegistry.INSTANCE.register(new DarkPurple().setName("darkpurple").setAliases("purple", "violet"));
        TagRegistry.INSTANCE.register(new Gold().setName("gold").setAliases("orange"));
        TagRegistry.INSTANCE.register(new Gray().setName("gray").setAliases("grey", "lightgray", "lightgrey"));
        TagRegistry.INSTANCE.register(new DarkGray().setName("darkgrey").setAliases("darkgray", "charcoal"));
        TagRegistry.INSTANCE.register(new Blue().setName("blue").setAliases("lightblue", "indigo"));
        TagRegistry.INSTANCE.register(new Green().setName("green").setAliases("brightgreen", "lightgreen", "lime"));
        TagRegistry.INSTANCE.register(new Aqua().setName("aqua").setAliases("cyan", "celeste", "diamond"));
        TagRegistry.INSTANCE.register(new Red().setName("red").setAliases("lightred", "salmon"));
        TagRegistry.INSTANCE.register(new LightPurple().setName("lightpurple").setAliases("magenta", "pink"));
        TagRegistry.INSTANCE.register(new Yellow().setName("yellow"));
        TagRegistry.INSTANCE.register(new White().setName("white"));
        TagRegistry.INSTANCE.register(new Obfuscated().setName("obfuscated").setAliases("random"));
        TagRegistry.INSTANCE.register(new Bold().setName("bold").setAliases("b"));
        TagRegistry.INSTANCE.register(new Strikethrough().setName("strikethrough").setAliases("strike", "s"));
        TagRegistry.INSTANCE.register(new Underline().setName("underline").setAliases("u"));
        TagRegistry.INSTANCE.register(new Italic().setName("italic").setAliases("italics", "i"));
        TagRegistry.INSTANCE.register(new Reset().setName("reset").setAliases("r"));
    }
}
