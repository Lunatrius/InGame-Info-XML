package com.github.lunatrius.ingameinfo;

import com.github.lunatrius.core.client.gui.FontRendererHelper;
import com.github.lunatrius.core.entity.EntityHelper;
import com.github.lunatrius.core.util.vector.Vector3f;
import com.github.lunatrius.core.util.vector.Vector3i;
import com.github.lunatrius.core.world.chunk.ChunkHelper;
import com.github.lunatrius.ingameinfo.parser.IParser;
import com.github.lunatrius.ingameinfo.parser.json.JsonParser;
import com.github.lunatrius.ingameinfo.parser.text.TextParser;
import com.github.lunatrius.ingameinfo.parser.xml.XmlParser;
import com.github.lunatrius.ingameinfo.serializer.ISerializer;
import com.github.lunatrius.ingameinfo.serializer.json.JsonSerializer;
import com.github.lunatrius.ingameinfo.serializer.text.TextSerializer;
import com.github.lunatrius.ingameinfo.serializer.xml.XmlSerializer;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.lunatrius.ingameinfo.Value.ValueType;

public class InGameInfoCore {
	public static final InGameInfoCore instance = new InGameInfoCore();

	private IParser parser;

	private Minecraft minecraftClient = Minecraft.getMinecraft();
	private MinecraftServer minecraftServer = null;
	private World world = null;
	private EntityClientPlayerMP player = null;
	private ScaledResolution scaledResolution = null;
	private File configDirectory = null;
	private File configFile = null;
	private final Map<Alignment, List<List<Value>>> format = new HashMap<Alignment, List<List<Value>>>();
	private final String[] roughdirection = {
			"South", "West", "North", "East"
	};
	private final String[] finedirection = {
			"South", "South West", "West", "North West", "North", "North East", "East", "South East"
	};
	private final String[] abrroughdirection = {
			"S", "W", "N", "E"
	};
	private final String[] abrfinedirection = {
			"S", "SW", "W", "NW", "N", "NE", "E", "SE"
	};
	private final Vector3i playerPosition = new Vector3i();
	private final Vector3f playerMotion = new Vector3f();
	private PotionEffect[] potionEffects = null;
	private boolean hasSeed;
	private long seed = 0;
	private final Map<Alignment, List<String>> valuePairs = new HashMap<Alignment, List<String>>();

	private InGameInfoCore() {
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
		return false;
	}

	public void setServer(MinecraftServer server) {
		this.minecraftServer = server;
		if (this.minecraftServer != null) {
			try {
				this.hasSeed = true;
				this.seed = this.minecraftServer.worldServerForDimension(0).getSeed();
			} catch (Exception e) {
				this.hasSeed = false;
				this.seed = 0;
			}
		}
	}

	public void onTickClient() {
		this.world = this.minecraftClient.theWorld;
		this.player = this.minecraftClient.thePlayer;

		this.scaledResolution = new ScaledResolution(this.minecraftClient.gameSettings, this.minecraftClient.displayWidth, this.minecraftClient.displayHeight);

		this.playerPosition.setX((int) Math.floor(this.player.posX));
		this.playerPosition.setY((int) Math.floor(this.player.posY));
		this.playerPosition.setZ((int) Math.floor(this.player.posZ));
		this.playerMotion.set((float) (this.player.posX - this.player.prevPosX), (float) (this.player.posY - this.player.prevPosY), (float) (this.player.posZ - this.player.prevPosZ));

		Collection<PotionEffect> potionEffectCollection = this.player.getActivePotionEffects();
		this.potionEffects = new PotionEffect[potionEffectCollection.size()];
		if (potionEffectCollection.size() > 0) {
			int index = 0;

			for (PotionEffect potionEffect : potionEffectCollection) {
				this.potionEffects[index++] = potionEffect;
			}
		}

		for (Alignment alignment : Alignment.values()) {
			List<List<Value>> lines = this.format.get(alignment);

			if (lines == null) {
				continue;
			}

			List<String> stringLines = new ArrayList<String>();
			this.valuePairs.put(alignment, stringLines);

			for (List<Value> line : lines) {
				String str = "";
				for (int i = 0; i < line.size(); i++) {
					str += getValue(line.get(i));
				}

				if (!str.isEmpty()) {
					stringLines.add(replaceVariables(str));
				}
			}
		}
	}

	public void onTickRender() {
		int x = 0, y = 0, type = -1;

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		for (Alignment alignment : Alignment.values()) {
			List<String> lines = this.valuePairs.get(alignment);

			if (lines == null) {
				continue;
			}

			y = alignment.getY(this.scaledResolution.getScaledHeight(), lines.size() * (this.minecraftClient.fontRenderer.FONT_HEIGHT + 1));
			for (String line : lines) {
				x = alignment.getX(this.scaledResolution.getScaledWidth(), this.minecraftClient.fontRenderer.getStringWidth(line));
				FontRendererHelper.drawLeftAlignedString(this.minecraftClient.fontRenderer, line, x, y, 0x00FFFFFF);
				y += this.minecraftClient.fontRenderer.FONT_HEIGHT + 1;
			}
		}

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public void copyDefaultConfig() {
		File configFile = new File(this.configDirectory, "InGameInfo.xml");

		if (!configFile.exists()) {
			try {
				ResourceLocation resourceLocation = new ResourceLocation("ingameinfo", "InGameInfo.xml");
				IResource resource = this.minecraftClient.getResourceManager().getResource(resourceLocation);
				InputStream inputStream = resource.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String defaultConfig = "";
				String line;

				while ((line = reader.readLine()) != null) {
					defaultConfig += line + System.getProperty("line.separator");
				}

				inputStream.close();

				FileWriter fileWriter = new FileWriter(configFile);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(defaultConfig);
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean loadConfig(String filename) {
		return setConfigFile(filename) && reloadConfig();
	}

	public boolean reloadConfig() {
		this.valuePairs.clear();
		this.format.clear();

		if (this.parser == null) {
			return false;
		}

		this.parser.load(this.configFile);
		if (!this.parser.parse(this.format)) {
			this.format.clear();
			return false;
		}

		return true;
	}

	public boolean saveConfig(String filename) {
		ISerializer serializer = null;
		File file = new File(this.configDirectory, filename);
		if (filename.endsWith(".xml")) {
			serializer = new XmlSerializer();
		} else if (filename.endsWith(".json")) {
			serializer = new JsonSerializer();
		} else if (filename.endsWith(".txt")) {
			serializer = new TextSerializer();
		}

		return serializer != null && serializer.save(file, this.format);
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
		if (value.type.equals(ValueType.STR)) {
			return value.value;
		} else if (value.type.equals(ValueType.NUM)) {
			return value.value;
		} else if (value.type.equals(ValueType.VAR)) {
			return getVariableValue(value.value);
		} else if (value.type.equals(ValueType.IF) && (value.values.size() == 2 || value.values.size() == 3)) {
			try {
				if (Boolean.parseBoolean(getValue(value.values.get(0)))) {
					return getValue(value.values.get(1));
				}
				if (value.values.size() > 2) {
					return getValue(value.values.get(2));
				}
				return "";
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.NOT) && value.values.size() == 1) {
			try {
				return Boolean.toString(!Boolean.parseBoolean(getValue(value.values.get(0))));
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.AND)) {
			try {
				for (Value operand : value.values) {
					if (!Boolean.parseBoolean(getValue(operand))) {
						return Boolean.toString(false);
					}
				}
				return Boolean.toString(true);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.OR)) {
			try {
				for (Value operand : value.values) {
					if (Boolean.parseBoolean(getValue(operand))) {
						return Boolean.toString(true);
					}
				}
				return Boolean.toString(false);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.XOR)) {
			try {
				boolean result = false;
				for (Value operand : value.values) {
					result = result ^ Boolean.parseBoolean(getValue(operand));
				}
				return Boolean.toString(result);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.GREATER) && value.values.size() > 1) {
			try {
				double current = Double.parseDouble(getValue(value.values.get(0)));

				for (Value operand : value.values.subList(1, value.values.size())) {
					double next = Double.parseDouble(getValue(operand));
					if (current > next) {
						current = next;
					} else {
						return Boolean.toString(false);
					}
				}
				return Boolean.toString(true);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.LESSER) && value.values.size() > 1) {
			try {
				double current = Double.parseDouble(getValue(value.values.get(0)));

				for (Value operand : value.values.subList(1, value.values.size())) {
					double next = Double.parseDouble(getValue(operand));
					if (current < next) {
						current = next;
					} else {
						return Boolean.toString(false);
					}
				}
				return Boolean.toString(true);
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type.equals(ValueType.EQUAL) && value.values.size() > 1) {
			try {
				double current = Double.parseDouble(getValue(value.values.get(0)));

				for (Value operand : value.values.subList(1, value.values.size())) {
					double next = Double.parseDouble(getValue(operand));
					if (current != next) {
						return Boolean.toString(false);
					}
				}
				return Boolean.toString(true);
			} catch (Exception e) {
				String current = getValue(value.values.get(0));

				for (Value operand : value.values.subList(1, value.values.size())) {
					String next = getValue(operand);
					if (!current.equals(next)) {
						return Boolean.toString(false);
					}
				}
				return Boolean.toString(true);
			}
		} else if (value.type.equals(ValueType.PCT) && value.values.size() == 2) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				return Double.toString(arg0 / arg1 * 100);
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type.equals(ValueType.CONCAT)) {
			String str = "";
			for (Value val : value.values) {
				str += getValue(val);
			}
			return str;
		} else if (value.type.equals(ValueType.MAX) && (value.values.size() == 2 || value.values.size() == 4)) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				int shift = value.values.size() - 2;
				return arg0 > arg1 ? getValue(value.values.get(0 + shift)) : getValue(value.values.get(1 + shift));
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type.equals(ValueType.MIN) && (value.values.size() == 2 || value.values.size() == 4)) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				int shift = value.values.size() - 2;
				return arg0 < arg1 ? getValue(value.values.get(0 + shift)) : getValue(value.values.get(1 + shift));
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type.equals(ValueType.ADD) && value.values.size() == 2) {
			try {
				int arg0 = Integer.parseInt(getValue(value.values.get(0)));
				int arg1 = Integer.parseInt(getValue(value.values.get(1)));
				return Integer.toString(arg0 + arg1);
			} catch (Exception e1) {
				try {
					double arg0 = Double.parseDouble(getValue(value.values.get(0)));
					double arg1 = Double.parseDouble(getValue(value.values.get(1)));
					return Double.toString(arg0 + arg1);
				} catch (Exception e2) {
					return "0";
				}
			}
		} else if (value.type.equals(ValueType.SUB) && value.values.size() == 2) {
			try {
				int arg0 = Integer.parseInt(getValue(value.values.get(0)));
				int arg1 = Integer.parseInt(getValue(value.values.get(1)));
				return Integer.toString(arg0 - arg1);
			} catch (Exception e1) {
				try {
					double arg0 = Double.parseDouble(getValue(value.values.get(0)));
					double arg1 = Double.parseDouble(getValue(value.values.get(1)));
					return Double.toString(arg0 - arg1);
				} catch (Exception e2) {
					return "0";
				}
			}
		} else if (value.type.equals(ValueType.MUL) && value.values.size() == 2) {
			try {
				int arg0 = Integer.parseInt(getValue(value.values.get(0)));
				int arg1 = Integer.parseInt(getValue(value.values.get(1)));
				return Integer.toString(arg0 * arg1);
			} catch (Exception e1) {
				try {
					double arg0 = Double.parseDouble(getValue(value.values.get(0)));
					double arg1 = Double.parseDouble(getValue(value.values.get(1)));
					return Double.toString(arg0 * arg1);
				} catch (Exception e2) {
					return "0";
				}
			}
		} else if (value.type.equals(ValueType.DIV) && value.values.size() == 2) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				return Double.toString(arg0 / arg1);
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.ROUND) && value.values.size() == 2) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				int arg1 = Integer.parseInt(getValue(value.values.get(1)));
				double dec = Math.pow(10, arg1);
				if (arg1 > 0) {
					return String.format(Locale.ENGLISH, "%." + arg1 + "f", arg0);
				}
				return Integer.toString((int) (Math.round(arg0 * dec) / dec));
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.MOD) && value.values.size() == 2) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				return Double.toString(Math.round((arg0 % arg1) * 10e6) / 10e6);
			} catch (Exception e2) {
				e2.printStackTrace();
				return "0";
			}
		} else if (value.type.equals(ValueType.MODI) && value.values.size() == 2) {
			try {
				int arg0 = Integer.parseInt(getValue(value.values.get(0)));
				int arg1 = Integer.parseInt(getValue(value.values.get(1)));
				return Integer.toString(arg0 % arg1);
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.ITEMQUANTITY) && (value.values.size() == 1 || value.values.size() == 2)) {
			try {
				Item item;
				int itemDamage = -1;
				try {
					String itemName = getValue(value.values.get(0));
					item = GameData.itemRegistry.get(itemName);
				} catch (Exception e3) {
					int itemID = Integer.parseInt(getValue(value.values.get(0)));
					item = GameData.itemRegistry.get(itemID);
				}
				if (value.values.size() == 2) {
					itemDamage = Integer.parseInt(getValue(value.values.get(1)));
				}
				return Integer.toString(EntityHelper.getItemCountInInventory(this.player.inventory, item, itemDamage));
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type.equals(ValueType.TRANS)) {
			try {
				return StatCollector.translateToLocal(value.value);
			} catch (Exception e) {
				return "?";
			}
		}

		return "";
	}

	private String getVariableValue(String var) {
		try {
			if (var.equalsIgnoreCase("day")) {
				return String.format(Locale.ENGLISH, "%d", this.world.getWorldTime() / 24000);
			} else if (var.equalsIgnoreCase("mctime") || var.equalsIgnoreCase("mctime24")) {
				long time = this.world.getWorldTime();
				long hour = (time / 1000 + 6) % 24;
				long minute = (time % 1000) * 60 / 1000;
				return String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);
			} else if (var.equalsIgnoreCase("mctime12")) {
				long time = this.world.getWorldTime();
				long hour = (time / 1000 + 6) % 24;
				long minute = (time % 1000) * 60 / 1000;
				String ampm = "AM";
				if (hour >= 12) {
					hour -= 12;
					ampm = "PM";
				}
				if (hour == 0) {
					hour += 12;
				}
				return String.format(Locale.ENGLISH, "%02d:%02d %s", hour, minute, ampm);
			} else if (var.equalsIgnoreCase("mctimeh")) {
				long hour = (this.world.getWorldTime() / 1000 + 6) % 24;
				return String.format(Locale.ENGLISH, "%02d", hour);
			} else if (var.equalsIgnoreCase("mctimem")) {
				long minute = (this.world.getWorldTime() % 1000) * 60 / 1000;
				return String.format(Locale.ENGLISH, "%02d", minute);
			} else if (var.equalsIgnoreCase("rltime") || var.equalsIgnoreCase("irltime") || var.equalsIgnoreCase("rltime24") || var.equalsIgnoreCase("irltime24")) {
				return (new SimpleDateFormat("HH:mm")).format(new Date());
			} else if (var.equalsIgnoreCase("rltime12") || var.equalsIgnoreCase("irltime12")) {
				return (new SimpleDateFormat("hh:mm a")).format(new Date());
			} else if (var.equalsIgnoreCase("light")) {
				try {
					return Integer.toString(this.world.getChunkFromBlockCoords(this.playerPosition.x, this.playerPosition.z).getBlockLightValue(this.playerPosition.x & 15, this.playerPosition.y, this.playerPosition.z & 15, this.world.calculateSkylightSubtracted(1.0f)));
				} catch (Exception e) {
					return "0";
				}
			} else if (var.equalsIgnoreCase("lightfeet")) {
				try {
					return Integer.toString(this.world.getChunkFromBlockCoords(this.playerPosition.x, this.playerPosition.z).getBlockLightValue(this.playerPosition.x & 15, (int) Math.round(this.player.boundingBox.minY), this.playerPosition.z & 15, this.world.calculateSkylightSubtracted(1.0f)));
				} catch (Exception e) {
					return "0";
				}
			} else if (var.equalsIgnoreCase("lightnosun")) {
				try {
					return Integer.toString(this.world.getChunkFromBlockCoords(this.playerPosition.x, this.playerPosition.z).getSavedLightValue(EnumSkyBlock.Block, this.playerPosition.x & 15, this.playerPosition.y, this.playerPosition.z & 15));
				} catch (Exception e) {
					return "0";
				}
			} else if (var.equalsIgnoreCase("lightnosunfeet")) {
				try {
					return Integer.toString(this.world.getChunkFromBlockCoords(this.playerPosition.x, this.playerPosition.z).getSavedLightValue(EnumSkyBlock.Block, this.playerPosition.x & 15, (int) Math.round(this.player.boundingBox.minY), this.playerPosition.z & 15));
				} catch (Exception e) {
					return "0";
				}
			} else if (var.equalsIgnoreCase("score")) {
				try {
					return Integer.toString(this.player.getScore());
				} catch (Exception var12) {
					return "0";
				}
			} else if (var.equalsIgnoreCase("x")) {
				return String.format(Locale.ENGLISH, "%.2f", this.player.posX);
			} else if (var.equalsIgnoreCase("y")) {
				return String.format(Locale.ENGLISH, "%.2f", this.player.posY);
			} else if (var.equalsIgnoreCase("yfeet")) {
				return String.format(Locale.ENGLISH, "%.2f", this.player.boundingBox.minY);
			} else if (var.equalsIgnoreCase("z")) {
				return String.format(Locale.ENGLISH, "%.2f", this.player.posZ);
			} else if (var.equalsIgnoreCase("xi")) {
				return Integer.toString(this.playerPosition.x);
			} else if (var.equalsIgnoreCase("yi")) {
				return Integer.toString(this.playerPosition.y);
			} else if (var.equalsIgnoreCase("yfeeti")) {
				return Integer.toString((int) Math.floor(this.player.boundingBox.minY));
			} else if (var.equalsIgnoreCase("zi")) {
				return Integer.toString(this.playerPosition.z);
			} else if (var.equalsIgnoreCase("speed")) {
				return String.format("%.2f", Math.sqrt(this.playerMotion.x * this.playerMotion.x + this.playerMotion.y * this.playerMotion.y + this.playerMotion.z * this.playerMotion.z));
			} else if (var.equalsIgnoreCase("speedx")) {
				return String.format("%.2f", Math.abs(this.playerMotion.x));
			} else if (var.equalsIgnoreCase("speedy")) {
				return String.format("%.2f", Math.abs(this.playerMotion.y));
			} else if (var.equalsIgnoreCase("speedz")) {
				return String.format("%.2f", Math.abs(this.playerMotion.z));
			} else if (var.equalsIgnoreCase("speedxz")) {
				return String.format("%.2f", Math.sqrt(this.playerMotion.x * this.playerMotion.x + this.playerMotion.z * this.playerMotion.z));
			} else if (var.equalsIgnoreCase("roughdirection")) {
				return this.roughdirection[MathHelper.floor_double(this.player.rotationYaw * 4.0 / 360.0 + 0.5) & 3];
			} else if (var.equalsIgnoreCase("finedirection")) {
				return this.finedirection[MathHelper.floor_double(this.player.rotationYaw * 8.0 / 360.0 + 0.5) & 7];
			} else if (var.equalsIgnoreCase("abrroughdirection")) {
				return this.abrroughdirection[MathHelper.floor_double(this.player.rotationYaw * 4.0 / 360.0 + 0.5) & 3];
			} else if (var.equalsIgnoreCase("abrfinedirection")) {
				return this.abrfinedirection[MathHelper.floor_double(this.player.rotationYaw * 8.0 / 360.0 + 0.5) & 7];
			} else if (var.equalsIgnoreCase("directionhud")) {
				int direction = MathHelper.floor_double(this.player.rotationYaw * 16.0f / 360.0f + 0.5) & 15;
				if (direction % 2 == 0) {
					return "\u00a7r" + this.abrfinedirection[(direction / 2 + this.abrfinedirection.length - 1) % this.abrfinedirection.length] + "   \u00a7c" + this.abrfinedirection[(direction / 2 + this.abrfinedirection.length) % this.abrfinedirection.length] + "\u00a7r   " + this.abrfinedirection[(direction / 2 + this.abrfinedirection.length + 1) % this.abrfinedirection.length];
				}
				return "\u00a7r  " + "   " + this.abrfinedirection[(direction / 2 + this.abrfinedirection.length) % this.abrfinedirection.length] + "   " + this.abrfinedirection[(direction / 2 + this.abrfinedirection.length + 1) % this.abrfinedirection.length] + "   ";
			} else if (var.equalsIgnoreCase("fps")) {
				return this.minecraftClient.debug.substring(0, this.minecraftClient.debug.indexOf(" fps"));
			} else if (var.equalsIgnoreCase("mouseovername")) {
				MovingObjectPosition objectMouseOver = this.minecraftClient.objectMouseOver;
				if (objectMouseOver != null) {
					if (objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
						return objectMouseOver.entityHit.func_145748_c_().getFormattedText();
					} else if (objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
						Block block = this.world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
						if (block != null) {
							ItemStack pickBlock = block.getPickBlock(objectMouseOver, this.world, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
							if (pickBlock != null) {
								return pickBlock.getDisplayName();
							}
							return block.getLocalizedName();
						}
					}
				}
				return "";
			} else if (var.equalsIgnoreCase("mouseoveruniquename")) {
				MovingObjectPosition objectMouseOver = this.minecraftClient.objectMouseOver;
				if (objectMouseOver != null) {
					if (objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
						return EntityList.getEntityString(objectMouseOver.entityHit);
					} else if (objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
						Block block = this.world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
						if (block != null) {
							return GameData.blockRegistry.getNameForObject(block);
						}
					}
				}
				return "";
			} else if (var.equalsIgnoreCase("mouseoverid")) {
				MovingObjectPosition objectMouseOver = this.minecraftClient.objectMouseOver;
				if (objectMouseOver != null) {
					if (objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
						return Integer.toString(objectMouseOver.entityHit.getEntityId());
					} else if (objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
						Block block = this.world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
						if (block != null) {
							return Integer.toString(GameData.blockRegistry.getId(block));
						}
					}
				}
				return "0";
			} else if (var.equalsIgnoreCase("mouseoverpowerweak")) {
				MovingObjectPosition objectMouseOver = this.minecraftClient.objectMouseOver;
				if (objectMouseOver != null) {
					if (objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
						Block block = this.world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
						if (block != null) {
							int power = -1;
							for (int side = 0; side < 6; side++) {
								power = Math.max(power, block.isProvidingWeakPower(this.world, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, side));
							}
							return Integer.toString(power);
						}
					}
				}
				return "-1";
			} else if (var.equalsIgnoreCase("mouseoverpowerstrong")) {
				MovingObjectPosition objectMouseOver = this.minecraftClient.objectMouseOver;
				if (objectMouseOver != null) {
					if (objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
						Block block = this.world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
						if (block != null) {
							int power = -1;
							for (int side = 0; side < 6; side++) {
								power = Math.max(power, block.isProvidingStrongPower(this.world, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, side));
							}
							return Integer.toString(power);
						}
					}
				}
				return "-1";
			} else if (var.equalsIgnoreCase("mouseoverpowerinput")) {
				MovingObjectPosition objectMouseOver = this.minecraftClient.objectMouseOver;
				if (objectMouseOver != null) {
					if (objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
						return Integer.toString(this.world.getBlockPowerInput(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
					}
				}
				return "-1";
			} else if (var.equalsIgnoreCase("worldname")) {
				return this.world.getWorldInfo().getWorldName();
			} else if (var.equalsIgnoreCase("worldsize")) {
				return Long.toString(this.world.getWorldInfo().getSizeOnDisk());
			} else if (var.equalsIgnoreCase("worldsizemb")) {
				return String.format(Locale.ENGLISH, "%.1f", this.world.getWorldInfo().getSizeOnDisk() / 1048576.0);
			} else if (var.equalsIgnoreCase("seed")) {
				return Long.toString(this.seed);
			} else if (var.equalsIgnoreCase("difficulty")) {
				return StatCollector.translateToLocal(this.minecraftClient.gameSettings.difficulty.getDifficultyResourceKey());
			} else if (var.equalsIgnoreCase("difficultyid")) {
				return Integer.toString(this.minecraftClient.gameSettings.difficulty.getDifficultyId());
			} else if (var.equalsIgnoreCase("gamemode")) {
				return StatCollector.translateToLocal("selectWorld.gameMode." + this.world.getWorldInfo().getGameType().getName());
			} else if (var.equalsIgnoreCase("gamemodeid")) {
				return Integer.toString(this.world.getWorldInfo().getGameType().getID());
			} else if (var.equalsIgnoreCase("healthpoints")) {
				return Float.toString(this.player.getHealth());
			} else if (var.equalsIgnoreCase("armorpoints")) {
				return Integer.toString(this.player.getTotalArmorValue());
			} else if (var.equalsIgnoreCase("foodpoints")) {
				return Integer.toString(this.player.getFoodStats().getFoodLevel());
			} else if (var.equalsIgnoreCase("foodsaturation")) {
				return Float.toString(this.player.getFoodStats().getSaturationLevel());
			} else if (var.equalsIgnoreCase("airticks")) {
				return Integer.toString(this.player.getAir());
			} else if (var.equalsIgnoreCase("playerlevel")) {
				return Integer.toString(this.player.experienceLevel);
			} else if (var.equalsIgnoreCase("xpthislevel")) {
				return Integer.toString((int) Math.ceil(this.player.experience * this.player.xpBarCap()));
			} else if (var.equalsIgnoreCase("xpuntilnext")) {
				return Integer.toString((int) Math.floor((1.0 - this.player.experience) * this.player.xpBarCap()));
			} else if (var.equalsIgnoreCase("xpcap")) {
				return Integer.toString(this.player.xpBarCap());
			} else if (var.equalsIgnoreCase("dimension")) {
				return this.world.provider.getDimensionName();
			} else if (var.equalsIgnoreCase("dimensionid")) {
				return Integer.toString(this.player.dimension);
			} else if (var.equalsIgnoreCase("biome")) {
				return this.world.getBiomeGenForCoords(this.playerPosition.x, this.playerPosition.z).biomeName;
			} else if (var.equalsIgnoreCase("biomeid")) {
				return Integer.toString(this.world.getBiomeGenForCoords(this.playerPosition.x, this.playerPosition.z).biomeID);
			} else if (var.equalsIgnoreCase("username")) {
				return this.player.getGameProfile().getName();
			} else if (var.equalsIgnoreCase("texturepack") || var.equalsIgnoreCase("resourcepack")) {
				// TODO: remove or figure out a way to display all resource packs
				// return this.minecraftClient.getResourcePackRepository().getResourcePackName();
			} else if (var.equalsIgnoreCase("entitiesrendered")) {
				String str = this.minecraftClient.getEntityDebug();
				return str.substring(str.indexOf(' ') + 1, str.indexOf('/'));
			} else if (var.equalsIgnoreCase("entitiestotal")) {
				String str = this.minecraftClient.getEntityDebug();
				return str.substring(str.indexOf('/') + 1, str.indexOf('.'));
			} else if (var.equalsIgnoreCase("daytime")) {
				return Boolean.toString(this.world.calculateSkylightSubtracted(1.0f) < 4);
			} else if (var.equalsIgnoreCase("raining")) {
				return Boolean.toString(this.world.getRainStrength(1.0f) > 0.2f && this.world.getBiomeGenForCoords(this.playerPosition.x, this.playerPosition.z).canSpawnLightningBolt());
			} else if (var.equalsIgnoreCase("thundering")) {
				return Boolean.toString(this.world.getWorldInfo().isThundering() && this.world.getBiomeGenForCoords(this.playerPosition.x, this.playerPosition.z).canSpawnLightningBolt());
			} else if (var.equalsIgnoreCase("snowing")) {
				BiomeGenBase biome = this.world.getBiomeGenForCoords(this.playerPosition.x, this.playerPosition.z);
				return Boolean.toString(this.world.isRaining() && !biome.canSpawnLightningBolt() && !biome.equals(BiomeGenBase.desert) && !biome.equals(BiomeGenBase.desertHills));
			} else if (var.equalsIgnoreCase("nextrain")) {
				if (this.minecraftServer == null) {
					return "?";
				}

				int seconds = this.minecraftServer.worldServers[0].getWorldInfo().getRainTime() / 20;
				if (seconds < 60) {
					return String.format(Locale.ENGLISH, "%ds", seconds);
				} else if (seconds < 3600) {
					return String.format(Locale.ENGLISH, "%dm", seconds / 60);
				}
				return String.format(Locale.ENGLISH, "%dh", seconds / 3600);
			} else if (var.equalsIgnoreCase("slimes")) {
				return Boolean.toString(this.hasSeed && ChunkHelper.isSlimeChunk(this.seed, this.playerPosition.x >> 4, this.playerPosition.z >> 4) || this.world.getBiomeGenForCoords(this.playerPosition.x, this.playerPosition.z).biomeID == BiomeGenBase.swampland.biomeID);
			} else if (var.equalsIgnoreCase("hardcore")) {
				return Boolean.toString(this.world.getWorldInfo().isHardcoreModeEnabled());
			} else if (var.equalsIgnoreCase("underwater") || var.equalsIgnoreCase("inwater")) {
				return Boolean.toString(this.player.isInWater());
			} else if (var.equalsIgnoreCase("wet")) {
				return Boolean.toString(this.player.isWet());
			} else if (var.equalsIgnoreCase("alive")) {
				return Boolean.toString(this.player.isEntityAlive());
			} else if (var.equalsIgnoreCase("burning")) {
				return Boolean.toString(this.player.isBurning());
			} else if (var.equalsIgnoreCase("riding")) {
				return Boolean.toString(this.player.isRiding());
			} else if (var.equalsIgnoreCase("sneaking")) {
				return Boolean.toString(this.player.isSneaking());
			} else if (var.equalsIgnoreCase("sprinting")) {
				return Boolean.toString(this.player.isSprinting());
			} else if (var.equalsIgnoreCase("invisible")) {
				return Boolean.toString(this.player.isInvisible());
			} else if (var.equalsIgnoreCase("eating")) {
				return Boolean.toString(this.player.isEating());
			} else if (var.equalsIgnoreCase("invulnerable")) {
				return Boolean.toString(this.player.isEntityInvulnerable());
			} else if (var.matches("(equipped|helmet|chestplate|leggings|boots)(uniquename|name|maxdamage|damage|damageleft)")) {
				ItemStack itemStack;

				if (var.startsWith("equipped")) {
					itemStack = this.player.getCurrentEquippedItem();
				} else {
					int slot = -1;
					if (var.startsWith("helmet")) {
						slot = 3;
					} else if (var.startsWith("chestplate")) {
						slot = 2;
					} else if (var.startsWith("leggings")) {
						slot = 1;
					} else if (var.startsWith("boots")) {
						slot = 0;
					}
					itemStack = this.player.inventory.armorItemInSlot(slot);
				}

				if (var.endsWith("uniquename")) {
					Item item = itemStack != null ? itemStack.getItem() : null;
					return item != null ? GameData.itemRegistry.getNameForObject(item) : "";
				} else if (var.endsWith("name")) {
					String arrows = itemStack != null && itemStack.getItem() == Items.bow ? " (" + EntityHelper.getItemCountInInventory(this.player.inventory, Items.arrow) + ")" : "";
					return itemStack != null ? itemStack.getDisplayName() + arrows : "";
				} else if (var.endsWith("maxdamage")) {
					return Integer.toString(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getMaxDamage() + 1 : 0);
				} else if (var.endsWith("damage")) {
					return Integer.toString(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getItemDamageForDisplay() : 0);
				} else if (var.endsWith("damageleft")) {
					return Integer.toString(itemStack != null && itemStack.isItemStackDamageable() ? itemStack.getMaxDamage() + 1 - itemStack.getItemDamageForDisplay() : 0);
				}
			} else if (var.equalsIgnoreCase("equippedquantity")) {
				ItemStack item = this.player.getCurrentEquippedItem();
				if (item != null) {
					return Integer.toString(EntityHelper.getItemCountInInventory(this.player.inventory, item.getItem(), item.getItemDamage()));
				}
				return "0";
			} else if (var.matches("potioneffect\\d+")) {
				int index = Integer.parseInt(var.substring(12));
				if (this.potionEffects.length > index) {
					String str = StatCollector.translateToLocal(this.potionEffects[index].getEffectName());
					switch (this.potionEffects[index].getAmplifier()) {
					case 1:
						str += " II";
						break;
					case 2:
						str += " III";
						break;
					case 3:
						str += " IV";
						break;
					}
					return str;
				}
				return "";
			} else if (var.matches("potionduration\\d+")) {
				int index = Integer.parseInt(var.substring(14));
				if (this.potionEffects.length > index) {
					return Potion.getDurationString(this.potionEffects[index]);
				}
				return "0:00";
			} else if (var.matches("potiondurationticks\\d+")) {
				int index = Integer.parseInt(var.substring(19));
				if (this.potionEffects.length > index) {
					return Integer.toString((this.potionEffects[index]).getDuration());
				}
				return "0";
			} else if (var.equalsIgnoreCase("memmax")) {
				return Long.toString(Runtime.getRuntime().maxMemory());
			} else if (var.equalsIgnoreCase("memtotal")) {
				return Long.toString(Runtime.getRuntime().totalMemory());
			} else if (var.equalsIgnoreCase("memfree")) {
				return Long.toString(Runtime.getRuntime().freeMemory());
			} else if (var.equalsIgnoreCase("memused")) {
				return Long.toString(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
			} else if (var.equalsIgnoreCase("server")) {
				String str = this.player.sendQueue.getNetworkManager().getSocketAddress().toString();
				int i = str.indexOf("/");
				int j = str.indexOf(":");
				if (i < 0) {
					return "localhost";
				}

				String name = (i == 0) ? str.substring(i + 1, j) : str.substring(0, i);
				String port = str.substring(j + 1);
				return name + (port.equals("25565") ? "" : ":" + port);
			} else if (var.equalsIgnoreCase("servername")) {
				String str = this.player.sendQueue.getNetworkManager().getSocketAddress().toString();
				int i = str.indexOf("/");
				if (i < 0) {
					return "localhost";
				} else if (i == 0) {
					return str.substring(i + 1, str.indexOf(":"));
				}
				return str.substring(0, i);
			} else if (var.equalsIgnoreCase("serverip")) {
				String str = this.player.sendQueue.getNetworkManager().getSocketAddress().toString();
				int i = str.indexOf("/");
				if (i < 0) {
					return "127.0.0.1";
				}
				return str.substring(i + 1, str.indexOf(":"));
			} else if (var.equalsIgnoreCase("serverport")) {
				String str = this.player.sendQueue.getNetworkManager().getSocketAddress().toString();
				int i = str.indexOf("/");
				if (i < 0) {
					return "-1";
				}
				return str.substring(str.indexOf(":") + 1);
			} else if (var.equalsIgnoreCase("black")) {
				return "\u00a70";
			} else if (var.equalsIgnoreCase("darkblue") || var.equalsIgnoreCase("navy")) {
				return "\u00a71";
			} else if (var.equalsIgnoreCase("darkgreen") || var.equalsIgnoreCase("green")) {
				return "\u00a72";
			} else if (var.equalsIgnoreCase("darkaqua") || var.equalsIgnoreCase("darkcyan") || var.equalsIgnoreCase("turquoise")) {
				return "\u00a73";
			} else if (var.equalsIgnoreCase("darkred")) {
				return "\u00a74";
			} else if (var.equalsIgnoreCase("purple") || var.equalsIgnoreCase("violet")) {
				return "\u00a75";
			} else if (var.equalsIgnoreCase("orange") || var.equalsIgnoreCase("gold")) {
				return "\u00a76";
			} else if (var.equalsIgnoreCase("lightgrey") || var.equalsIgnoreCase("lightgray") || var.equalsIgnoreCase("grey") || var.equalsIgnoreCase("gray")) {
				return "\u00a77";
			} else if (var.equalsIgnoreCase("darkgrey") || var.equalsIgnoreCase("darkgray") || var.equalsIgnoreCase("charcoal")) {
				return "\u00a78";
			} else if (var.equalsIgnoreCase("blue") || var.equalsIgnoreCase("lightblue") || var.equalsIgnoreCase("indigo")) {
				return "\u00a79";
			} else if (var.equalsIgnoreCase("brightgreen") || var.equalsIgnoreCase("lightgreen") || var.equalsIgnoreCase("lime")) {
				return "\u00a7a";
			} else if (var.equalsIgnoreCase("aqua") || var.equalsIgnoreCase("cyan") || var.equalsIgnoreCase("celeste") || var.equalsIgnoreCase("diamond")) {
				return "\u00a7b";
			} else if (var.equalsIgnoreCase("red") || var.equalsIgnoreCase("lightred") || var.equalsIgnoreCase("salmon")) {
				return "\u00a7c";
			} else if (var.equalsIgnoreCase("magenta") || var.equalsIgnoreCase("pink")) {
				return "\u00a7d";
			} else if (var.equalsIgnoreCase("yellow")) {
				return "\u00a7e";
			} else if (var.equalsIgnoreCase("white")) {
				return "\u00a7f";
			} else if (var.equalsIgnoreCase("random")) {
				return "\u00a7k";
			} else if (var.equalsIgnoreCase("bold") || var.equalsIgnoreCase("b")) {
				return "\u00a7l";
			} else if (var.equalsIgnoreCase("strikethrough") || var.equalsIgnoreCase("strike") || var.equalsIgnoreCase("s")) {
				return "\u00a7m";
			} else if (var.equalsIgnoreCase("underline") || var.equalsIgnoreCase("u")) {
				return "\u00a7n";
			} else if (var.equalsIgnoreCase("italic") || var.equalsIgnoreCase("italics") || var.equalsIgnoreCase("i")) {
				return "\u00a7o";
			} else if (var.equalsIgnoreCase("reset") || var.equalsIgnoreCase("r")) {
				return "\u00a7r";
			}
		} catch (Exception e) {
			return "null";
		}

		return "{" + var + "}";
	}
}
