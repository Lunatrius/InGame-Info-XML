package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;

public abstract class TagFormatting extends Tag {
	private static final String SIGN = "\u00a7";

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

	public static class Purple extends TagFormatting {
		@Override
		public String getValue() {
			return SIGN + "5";
		}
	}

	public static class Orange extends TagFormatting {
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

	public static class Magenta extends TagFormatting {
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
		TagRegistry.INSTANCE.register(new Black(), "black");
		TagRegistry.INSTANCE.register(new DarkBlue(), "darkblue", "navy");
		TagRegistry.INSTANCE.register(new DarkGreen(), "darkgreen", "green");
		TagRegistry.INSTANCE.register(new DarkAqua(), "darkaqua", "darkcyan", "turquoise");
		TagRegistry.INSTANCE.register(new DarkRed(), "darkred");
		TagRegistry.INSTANCE.register(new Purple(), "purple", "violet");
		TagRegistry.INSTANCE.register(new Orange(), "orange", "gold");
		TagRegistry.INSTANCE.register(new Gray(), "gray", "grey", "lightgray", "lightgrey");
		TagRegistry.INSTANCE.register(new DarkGray(), "darkgrey", "darkgray", "charcoal");
		TagRegistry.INSTANCE.register(new Blue(), "blue", "lightblue", "indigo");
		TagRegistry.INSTANCE.register(new Green(), "brightgreen", "lightgreen", "lime");
		TagRegistry.INSTANCE.register(new Aqua(), "aqua", "cyan", "celeste", "diamond");
		TagRegistry.INSTANCE.register(new Red(), "red", "lightred", "salmon");
		TagRegistry.INSTANCE.register(new Magenta(), "magenta", "pink");
		TagRegistry.INSTANCE.register(new Yellow(), "yellow");
		TagRegistry.INSTANCE.register(new White(), "white");
		TagRegistry.INSTANCE.register(new Obfuscated(), "random", "obfuscated");
		TagRegistry.INSTANCE.register(new Bold(), "bold", "b");
		TagRegistry.INSTANCE.register(new Strikethrough(), "strikethrough", "strike", "s");
		TagRegistry.INSTANCE.register(new Underline(), "underline", "u");
		TagRegistry.INSTANCE.register(new Italic(), "italic", "italics", "i");
		TagRegistry.INSTANCE.register(new Reset(), "reset", "r");
	}
}
