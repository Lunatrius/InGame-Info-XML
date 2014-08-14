package com.github.lunatrius.ingameinfo;

import com.github.lunatrius.core.entity.EntityHelper;
import com.github.lunatrius.ingameinfo.client.gui.Info;
import com.github.lunatrius.ingameinfo.client.gui.InfoIcon;
import com.github.lunatrius.ingameinfo.client.gui.InfoItem;
import com.github.lunatrius.ingameinfo.client.gui.InfoText;
import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import com.github.lunatrius.ingameinfo.parser.IParser;
import com.github.lunatrius.ingameinfo.parser.json.JsonParser;
import com.github.lunatrius.ingameinfo.parser.text.TextParser;
import com.github.lunatrius.ingameinfo.parser.xml.XmlParser;
import com.github.lunatrius.ingameinfo.printer.IPrinter;
import com.github.lunatrius.ingameinfo.printer.json.JsonPrinter;
import com.github.lunatrius.ingameinfo.printer.text.TextPrinter;
import com.github.lunatrius.ingameinfo.printer.xml.XmlPrinter;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.tag.Tag;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.lunatrius.ingameinfo.Value.ValueType;

public class InGameInfoCore {
	public static final InGameInfoCore INSTANCE = new InGameInfoCore();

	private IParser parser;

	private final Minecraft minecraft = Minecraft.getMinecraft();
	private final Profiler profiler = this.minecraft.mcProfiler;
	private File configDirectory = null;
	private File configFile = null;
	private final Map<Alignment, List<List<Value>>> format = new HashMap<Alignment, List<List<Value>>>();
	private final List<Info> info = new ArrayList<Info>();
	private final List<Info> infoItemQueue = new ArrayList<Info>();

	private InGameInfoCore() {
		Tag.setInfo(this.infoItemQueue);
	}

	public boolean setConfigDirectory(File directory) {
		this.configDirectory = directory;
		return true;
	}

	public File getConfigDirectory() {
		return this.configDirectory;
	}

	public boolean setConfigFile(String filename) {
		File file = new File(this.configDirectory, filename);
		if (file.exists()) {
			if (filename.endsWith(".xml")) {
				this.configFile = file;
				this.parser = new XmlParser();
				return true;
			} else if (filename.endsWith(".json")) {
				this.configFile = file;
				this.parser = new JsonParser();
				return true;
			} else if (filename.endsWith(".txt")) {
				this.configFile = file;
				this.parser = new TextParser();
				return true;
			}
		}

		this.configFile = null;
		this.parser = new XmlParser();
		return false;
	}

	public void onTickClient() {
		ScaledResolution scaledResolution = new ScaledResolution(this.minecraft, this.minecraft.displayWidth, this.minecraft.displayHeight);
		int scaledWidth = (int) (scaledResolution.getScaledWidth() / ConfigurationHandler.scale);
		int scaledHeight = (int) (scaledResolution.getScaledHeight() / ConfigurationHandler.scale);

		World world = this.minecraft.theWorld;
		if (world == null) {
			return;
		}
		Tag.setWorld(world);

		EntityClientPlayerMP player = this.minecraft.thePlayer;
		if (player == null) {
			return;
		}
		Tag.setPlayer(player);

		this.info.clear();
		int x, y;

		this.profiler.startSection("alignment");
		this.profiler.startSection("none");
		for (Alignment alignment : Alignment.values()) {
			this.profiler.endStartSection(alignment.toString().toLowerCase());
			List<List<Value>> lines = this.format.get(alignment);

			if (lines == null) {
				continue;
			}

			FontRenderer fontRenderer = this.minecraft.fontRenderer;
			List<Info> queue = new ArrayList<Info>();

			for (List<Value> line : lines) {
				String str = "";

				this.infoItemQueue.clear();
				this.profiler.startSection("taggathering");
				for (Value value : line) {
					str += getReplacedValue(value);
				}
				this.profiler.endSection();

				if (!str.isEmpty()) {
					String processed = str.replaceAll("\\{ICON\\|( *)\\}", "$1");

					x = alignment.getX(scaledWidth, fontRenderer.getStringWidth(processed));
					InfoText text = new InfoText(fontRenderer, processed, x, 0);

					if (this.infoItemQueue.size() > 0) {
						Pattern pattern = Pattern.compile("\\{ICON\\|( *)\\}", Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(str);

						for (int i = 0; i < this.infoItemQueue.size() && matcher.find(); i++) {
							Info item = this.infoItemQueue.get(i);
							item.x = fontRenderer.getStringWidth(str.substring(0, matcher.start()));
							text.children.add(item);

							str = str.replaceFirst(Pattern.quote(matcher.group(0)), matcher.group(1));
							matcher.reset(str);
						}
					}
					queue.add(text);
				}
			}

			y = alignment.getY(scaledHeight, queue.size() * (fontRenderer.FONT_HEIGHT + 1));
			for (Info item : queue) {
				item.y = y;
				this.info.add(item);
				y += fontRenderer.FONT_HEIGHT + 1;
			}

			this.info.addAll(queue);
		}
		this.profiler.endSection();
		this.profiler.endSection();

		Tag.releaseResources();
	}

	public void onTickRender() {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glScalef(ConfigurationHandler.scale, ConfigurationHandler.scale, ConfigurationHandler.scale);

		for (Info info : this.info) {
			info.draw();
		}

		GL11.glScalef(1.0f / ConfigurationHandler.scale, 1.0f / ConfigurationHandler.scale, 1.0f / ConfigurationHandler.scale);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public boolean loadConfig(String filename) {
		return setConfigFile(filename) && reloadConfig();
	}

	public boolean reloadConfig() {
		this.info.clear();
		this.infoItemQueue.clear();
		this.format.clear();

		if (this.parser == null) {
			return false;
		}

		final InputStream inputStream = getInputStream();
		if (inputStream == null) {
			return false;
		}

		if (this.parser.load(inputStream) && this.parser.parse(this.format)) {
			return true;
		}

		this.format.clear();
		return false;
	}

	private InputStream getInputStream() {
		InputStream inputStream = null;

		try {
			if (this.configFile != null && this.configFile.exists()) {
				Reference.logger.debug("Loading file config...");
				inputStream = new FileInputStream(this.configFile);
			} else {
				Reference.logger.debug("Loading default config...");
				ResourceLocation resourceLocation = new ResourceLocation("ingameinfo", "InGameInfo.xml");
				IResource resource = this.minecraft.getResourceManager().getResource(resourceLocation);
				inputStream = resource.getInputStream();
			}
		} catch (Exception e) {
			Reference.logger.error("", e);
		}

		return inputStream;
	}

	public boolean saveConfig(String filename) {
		IPrinter printer = null;
		File file = new File(this.configDirectory, filename);
		if (filename.endsWith(".xml")) {
			printer = new XmlPrinter();
		} else if (filename.endsWith(".json")) {
			printer = new JsonPrinter();
		} else if (filename.endsWith(".txt")) {
			printer = new TextPrinter();
		}

		return printer != null && printer.print(file, this.format);
	}

	private String replaceVariables(String str) {
		Pattern pattern = Pattern.compile("\\{([a-z0-9]+)\\}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);

		while (matcher.find()) {
			str = str.replace(matcher.group(0), getVariableValue(matcher.group(1)));
		}
		return str;
	}

	private String getValue(Value value) {
		int size = value.values.size();
		if (!value.type.validSize(size)) {
			return "";
		}

		if (value.type.equals(ValueType.STR)) {
			return value.value;
		} else if (value.type.equals(ValueType.NUM)) {
			return value.value;
		} else if (value.type.equals(ValueType.VAR)) {
			return getVariableValue(value.value);
		} else if (value.type.equals(ValueType.IF)) {
			try {
				if (getBooleanValue(value, 0)) {
					return getValue(value, 1);
				}
				if (size > 2) {
					return getValue(value, 2);
				}
				return "";
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.NOT)) {
			try {
				return String.valueOf(!getBooleanValue(value, 0));
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.AND)) {
			try {
				for (Value operand : value.values) {
					if (!getBooleanValue(operand)) {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.OR)) {
			try {
				for (Value operand : value.values) {
					if (getBooleanValue(operand)) {
						return String.valueOf(true);
					}
				}
				return String.valueOf(false);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.XOR)) {
			try {
				boolean result = false;
				for (Value operand : value.values) {
					result = result ^ getBooleanValue(operand);
				}
				return String.valueOf(result);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.GREATER)) {
			try {
				double current = getDoubleValue(value, 0);

				for (Value operand : value.values.subList(1, size)) {
					double next = getDoubleValue(operand);
					if (current > next) {
						current = next;
					} else {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.LESSER)) {
			try {
				double current = getDoubleValue(value, 0);

				for (Value operand : value.values.subList(1, size)) {
					double next = getDoubleValue(operand);
					if (current < next) {
						current = next;
					} else {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.EQUAL)) {
			try {
				double current = getDoubleValue(value, 0);

				for (Value operand : value.values.subList(1, size)) {
					double next = getDoubleValue(operand);
					if (current != next) {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			} catch (Exception e) {
				String current = getValue(value, 0);

				for (Value operand : value.values.subList(1, size)) {
					String next = getReplacedValue(operand);
					if (!current.equals(next)) {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			}
		} else if (value.type.equals(ValueType.PCT)) {
			try {
				double arg0 = getDoubleValue(value, 0);
				double arg1 = getDoubleValue(value, 1);
				return String.valueOf(arg0 / arg1 * 100);
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type.equals(ValueType.CONCAT)) {
			String str = "";
			for (Value val : value.values) {
				str += getReplacedValue(val);
			}
			return str;
		} else if (value.type.equals(ValueType.OPERATION)) {
			try {
				Operation operation = Operation.fromString(getValue(value, 0));
				double base;
				int operandCount = (size - 2) / 2;
				if (operation.equals(Operation.GT)) {
					base = getDoubleValue(value, 1);
					for (int i = 2; i < 2 + operandCount; i++) {
						double operand = getDoubleValue(value, i);
						if (base > operand) {
							return getValue(value, operandCount + i);
						}
					}
					return size % 2 == 0 ? "" : getValue(value, size - 1);
				} else if (operation.equals(Operation.LT)) {
					base = getDoubleValue(value, 1);
					for (int i = 2; i < 2 + operandCount; i++) {
						double operand = getDoubleValue(value, i);
						if (base < operand) {
							return getValue(value, operandCount + i);
						}
					}
					return size % 2 == 0 ? "" : getValue(value, size - 1);
				} else if (operation.equals(Operation.GE)) {
					base = getDoubleValue(value, 1);
					for (int i = 2; i < 2 + operandCount; i++) {
						double operand = getDoubleValue(value, i);
						if (base >= operand) {
							return getValue(value, operandCount + i);
						}
					}
					return size % 2 == 0 ? "" : getValue(value, size - 1);
				} else if (operation.equals(Operation.LE)) {
					base = getDoubleValue(value, 1);
					for (int i = 2; i < 2 + operandCount; i++) {
						double operand = getDoubleValue(value, i);
						if (base <= operand) {
							return getValue(value, operandCount + i);
						}
					}
					return size % 2 == 0 ? "" : getValue(value, size - 1);
				} else if (operation.equals(Operation.EQ)) {
					try {
						base = getDoubleValue(value, 1);
						for (int i = 2; i < 2 + operandCount; i++) {
							double operand = getDoubleValue(value, i);
							if (base == operand) {
								return getValue(value, operandCount + i);
							}
						}
						return size % 2 == 0 ? "" : getValue(value, size - 1);
					} catch (NumberFormatException e) {
						String basestr = getValue(value, 1);
						for (int i = 2; i < 2 + operandCount; i++) {
							String operand = getValue(value, i);
							if (basestr.equals(operand)) {
								return getValue(value, operandCount + i);
							}
						}
						return size % 2 == 0 ? "" : getValue(value, size - 1);
					}
				} else if (operation.equals(Operation.NE)) {
					try {
						base = getDoubleValue(value, 1);
						for (int i = 2; i < 2 + operandCount; i++) {
							double operand = getDoubleValue(value, i);
							if (base != operand) {
								return getValue(value, operandCount + i);
							}
						}
						return size % 2 == 0 ? "" : getValue(value, size - 1);
					} catch (NumberFormatException e) {
						String basestr = getValue(value, 1);
						for (int i = 2; i < 2 + operandCount; i++) {
							String operand = getValue(value, i);
							if (!basestr.equals(operand)) {
								return getValue(value, operandCount + i);
							}
						}
						return size % 2 == 0 ? "" : getValue(value, size - 1);
					}
				}

				return "";
			} catch (Exception e) {
				return "";
			}
		} else if (value.type.equals(ValueType.MAX)) {
			try {
				double arg0 = getDoubleValue(value, 0);
				double arg1 = getDoubleValue(value, 1);
				int shift = size - 2;
				return arg0 > arg1 ? getValue(value, 0 + shift) : getValue(value, 1 + shift);
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type.equals(ValueType.MIN)) {
			try {
				double arg0 = getDoubleValue(value, 0);
				double arg1 = getDoubleValue(value, 1);
				int shift = size - 2;
				return arg0 < arg1 ? getValue(value, 0 + shift) : getValue(value, 1 + shift);
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type.equals(ValueType.ADD)) {
			try {
				int arg0 = getIntValue(value, 0);
				int arg1 = getIntValue(value, 1);
				return String.valueOf(arg0 + arg1);
			} catch (Exception e1) {
				try {
					double arg0 = getDoubleValue(value, 0);
					double arg1 = getDoubleValue(value, 1);
					return String.valueOf(arg0 + arg1);
				} catch (Exception e2) {
					return "0";
				}
			}
		} else if (value.type.equals(ValueType.SUB)) {
			try {
				int arg0 = getIntValue(value, 0);
				int arg1 = getIntValue(value, 1);
				return String.valueOf(arg0 - arg1);
			} catch (Exception e1) {
				try {
					double arg0 = getDoubleValue(value, 0);
					double arg1 = getDoubleValue(value, 1);
					return String.valueOf(arg0 - arg1);
				} catch (Exception e2) {
					return "0";
				}
			}
		} else if (value.type.equals(ValueType.MUL)) {
			try {
				int arg0 = getIntValue(value, 0);
				int arg1 = getIntValue(value, 1);
				return String.valueOf(arg0 * arg1);
			} catch (Exception e1) {
				try {
					double arg0 = getDoubleValue(value, 0);
					double arg1 = getDoubleValue(value, 1);
					return String.valueOf(arg0 * arg1);
				} catch (Exception e2) {
					return "0";
				}
			}
		} else if (value.type.equals(ValueType.DIV)) {
			try {
				double arg0 = getDoubleValue(value, 0);
				double arg1 = getDoubleValue(value, 1);
				return String.valueOf(arg0 / arg1);
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.ROUND)) {
			try {
				double arg0 = getDoubleValue(value, 0);
				int arg1 = getIntValue(value, 1);
				double dec = Math.pow(10, arg1);
				if (arg1 > 0) {
					return String.format(Locale.ENGLISH, "%." + arg1 + "f", arg0);
				}
				return String.valueOf((int) (Math.round(arg0 * dec) / dec));
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.MOD)) {
			try {
				double arg0 = getDoubleValue(value, 0);
				double arg1 = getDoubleValue(value, 1);
				return String.valueOf(Math.round((arg0 % arg1) * 10e6) / 10e6);
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.MODI)) {
			try {
				int arg0 = getIntValue(value, 0);
				int arg1 = getIntValue(value, 1);
				return String.valueOf(arg0 % arg1);
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.ITEMQUANTITY)) {
			try {
				Item item;
				int itemDamage = -1;
				try {
					item = GameData.getItemRegistry().getObject(getValue(value, 0));
				} catch (Exception e3) {
					item = GameData.getItemRegistry().getObjectById(getIntValue(value, 0));
				}
				if (size == 2) {
					itemDamage = getIntValue(value, 1);
				}
				return String.valueOf(EntityHelper.getItemCountInInventory(this.minecraft.thePlayer.inventory, item, itemDamage));
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.TRANS)) {
			try {
				String format = getValue(value, 0);
				String[] args = new String[size - 1];
				for (int i = 0; i < args.length; i++) {
					args[i] = getValue(value, i + 1);
				}
				return I18n.format(format, args);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.FORMATTEDTIME)) {
			try {
				String format = getValue(value, 0);
				return new SimpleDateFormat(format).format(new Date());
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.ICON)) {
			try {
				String what = getValue(value, 0);

				if (size == 1 || size == 2) {
					InfoItem item;
					ItemStack itemStack;

					int metadata = 0;
					if (size == 2) {
						metadata = getIntValue(value, 1);
						// TODO: this needs a better workaround
						Block block = GameData.getBlockRegistry().getObject(what);
						if (block == Blocks.double_plant) {
							metadata &= 7;
						}
					}

					itemStack = new ItemStack(GameData.getItemRegistry().getObject(what), 1, metadata);
					if (itemStack.getItem() != null) {
						item = new InfoItem(this.minecraft.fontRenderer, itemStack);
						this.infoItemQueue.add(item);
						return Tag.getIconTag(item);
					}

					itemStack = new ItemStack(GameData.getBlockRegistry().getObject(what), 1, metadata);
					if (itemStack.getItem() != null) {
						item = new InfoItem(this.minecraft.fontRenderer, itemStack);
						this.infoItemQueue.add(item);
						return Tag.getIconTag(item);
					}
				}

				InfoIcon icon = new InfoIcon(what);
				int index = 0;

				if (size == 5 || size == 11) {
					int displayX = getIntValue(value, ++index);
					int displayY = getIntValue(value, ++index);
					int displayWidth = getIntValue(value, ++index);
					int displayHeight = getIntValue(value, ++index);
					icon.setDisplayDimensions(displayX, displayY, displayWidth, displayHeight);
				}

				if (size == 7 || size == 11) {
					int iconX = getIntValue(value, ++index);
					int iconY = getIntValue(value, ++index);
					int iconWidth = getIntValue(value, ++index);
					int iconHeight = getIntValue(value, ++index);
					int textureWidth = getIntValue(value, ++index);
					int textureHeight = getIntValue(value, ++index);
					icon.setTextureData(iconX, iconY, iconWidth, iconHeight, textureWidth, textureHeight);
				}

				this.infoItemQueue.add(icon);
				return Tag.getIconTag(icon);
			} catch (Exception e) {
				return "?";
			}
		}

		return "";
	}

	private String getReplacedValue(Value value) {
		return replaceVariables(getValue(value));
	}

	private String getValue(Value value, int index) {
		return getReplacedValue(value.values.get(index));
	}

	private int getIntValue(Value value) {
		return Integer.parseInt(getReplacedValue(value));
	}

	private int getIntValue(Value value, int index) {
		return Integer.parseInt(getValue(value, index));
	}

	private double getDoubleValue(Value value) {
		return Double.parseDouble(getReplacedValue(value));
	}

	private double getDoubleValue(Value value, int index) {
		return Double.parseDouble(getValue(value, index));
	}

	private boolean getBooleanValue(Value value) {
		return Boolean.parseBoolean(getReplacedValue(value));
	}

	private boolean getBooleanValue(Value value, int index) {
		return Boolean.parseBoolean(getValue(value, index));
	}

	private String getVariableValue(String var) {
		try {
			String value = TagRegistry.INSTANCE.getValue(var);
			if (value != null) {
				return value;
			}
		} catch (Exception e) {
			return "null";
		}

		return "{" + var + "}";
	}
}
