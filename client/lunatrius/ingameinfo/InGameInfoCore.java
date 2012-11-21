package lunatrius.ingameinfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.World;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InGameInfoCore {
	private final static InGameInfoCore instance = new InGameInfoCore();
	private Logger logger = null;

	private Minecraft minecraftClient = null;
	private MinecraftServer minecraftServer = null;
	private World world = null;
	private EntityPlayer player = null;
	private ScaledResolution scaledResolution = null;
	private final StringTranslate strTranslate = StringTranslate.getInstance();
	private File configFile = null;
	private final Map<String, List<List<Value>>> format = new HashMap<String, List<List<Value>>>();
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
	private final int[] playerPosition = new int[] {
			0, 0, 0
	};
	private PotionEffect[] potionEffects = null;
	private long seed = 0;
	private final Map<String, List<String>> valuePairs = new HashMap<String, List<String>>();

	private InGameInfoCore() {
	}

	public static InGameInfoCore instance() {
		return instance;
	}

	public void init(File file) {
		this.configFile = file;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setServer(MinecraftServer server) {
		this.minecraftServer = server;
		if (this.minecraftServer != null) {
			try {
				this.seed = this.minecraftServer.worldServers[0].getSeed();
			} catch (Exception e) {
				this.seed = 0;
			}
		}

	}

	public void setClient(Minecraft client) {
		this.minecraftClient = client;
	}

	public void onTickClient() {
		this.world = this.minecraftClient.theWorld;
		this.player = this.minecraftClient.thePlayer;

		this.scaledResolution = new ScaledResolution(this.minecraftClient.gameSettings, this.minecraftClient.displayWidth, this.minecraftClient.displayHeight);

		this.playerPosition[0] = (int) Math.floor(this.player.posX);
		this.playerPosition[1] = (int) Math.floor(this.player.posY);
		this.playerPosition[2] = (int) Math.floor(this.player.posZ);

		Collection potionEffectCollection = this.player.getActivePotionEffects();
		this.potionEffects = new PotionEffect[potionEffectCollection.size()];
		if (potionEffectCollection.size() > 0) {
			int index = 0;

			Iterator<PotionEffect> iterator = potionEffectCollection.iterator();
			while (iterator.hasNext()) {
				this.potionEffects[index++] = iterator.next();
			}
		}

		Set<String> keys = this.format.keySet();
		for (String key : keys) {
			List<List<Value>> lines = this.format.get(key);

			List<String> stringLines = new ArrayList<String>();
			this.valuePairs.put(key, stringLines);

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

		Set<String> keys = this.valuePairs.keySet();
		for (String key : keys) {
			List<String> lines = this.valuePairs.get(key);

			if (lines == null) {
				continue;
			}

			if (key.contains("top")) {
				y = 2;
			} else if (key.contains("mid")) {
				y = this.scaledResolution.getScaledHeight() / 2 - lines.size() * 10 / 2;
			} else if (key.contains("bot")) {
				y = this.scaledResolution.getScaledHeight() - lines.size() * 10 - 2;
			} else {
				continue;
			}

			if (key.contains("left")) {
				x = 2;
				type = 0;
			} else if (key.contains("center")) {
				x = this.scaledResolution.getScaledWidth() / 2;
				type = 1;
			} else if (key.contains("right")) {
				x = this.scaledResolution.getScaledWidth() - 2;
				type = 2;
			} else {
				continue;
			}

			for (String line : lines) {
				switch (type) {
				case 0:
					drawLeftAlignedString(this.minecraftClient.fontRenderer, line, x, y, 0x00FFFFFF);
					break;
				case 1:
					drawCenteredString(this.minecraftClient.fontRenderer, line, x, y, 0x00FFFFFF);
					break;
				case 2:
					drawRightAlignedString(this.minecraftClient.fontRenderer, line, x, y, 0x00FFFFFF);
					break;
				}

				y += 10;
			}
		}
	}

	public boolean loadConfig() {
		try {
			this.format.clear();

			if (!this.configFile.exists()) {
				try {
					String assetsDir = "lunatrius/ingameinfo/assets/";
					InputStream stream = InGameInfoCore.class.getClassLoader().getResourceAsStream(assetsDir + this.configFile.getName());

					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					String defaultConfig = "";
					String line = null;

					while ((line = reader.readLine()) != null) {
						defaultConfig += line + System.getProperty("line.separator");
					}

					FileWriter fstream = new FileWriter(this.configFile);
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(defaultConfig);
					out.close();
				} catch (Exception e) {
					this.logger.log(Level.SEVERE, "Could not extract default configuration - corrupted installation detected!", e);
					throw new RuntimeException(e);
				}
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(this.configFile);
			doc.getDocumentElement().normalize();

			NodeList nodeListLines = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < nodeListLines.getLength(); i++) {
				if (nodeListLines.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element elementLines = (Element) nodeListLines.item(i);

				if (!elementLines.getNodeName().matches("(?i)^lines$")) {
					continue;
				}

				List<List<Value>> listLines = new ArrayList<List<Value>>();

				String attributeAt = elementLines.getAttribute("at");
				String at = "";

				if (attributeAt.matches("(?i).*(top).*")) {
					at = "top";
				} else if (attributeAt.matches("(?i).*(mid).*")) {
					at = "mid";
				} else if (attributeAt.matches("(?i).*(bot).*")) {
					at = "bot";
				} else {
					continue;
				}

				if (attributeAt.matches("(?i).*(left).*")) {
					at += "left";
				} else if (attributeAt.matches("(?i).*(center).*")) {
					at += "center";
				} else if (attributeAt.matches("(?i).*(right).*")) {
					at += "right";
				} else {
					continue;
				}

				NodeList nodeListLine = elementLines.getChildNodes();
				for (int j = 0; j < nodeListLine.getLength(); j++) {
					if (nodeListLine.item(j).getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}

					Element elementLine = (Element) nodeListLine.item(j);

					if (!elementLine.getNodeName().matches("(?i)^line$")) {
						continue;
					}

					listLines.add(getValues(elementLine));
				}

				this.format.put(at, listLines);
			}

			return true;
		} catch (Exception e) {
			this.format.clear();
			e.printStackTrace();
		}
		return false;
	}

	private String replaceVariables(String str) {
		Pattern pattern = Pattern.compile("\\{([a-z0-9]+)\\}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);

		while (matcher.find()) {
			str = str.replace(matcher.group(0), getVariableValue(matcher.group(1)));
		}
		return str;
	}

	private List<Value> getValues(Element element) {
		List<Value> values = new ArrayList<Value>();

		NodeList nodeListValues = element.getChildNodes();
		for (int i = 0; i < nodeListValues.getLength(); i++) {
			if (nodeListValues.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element elementValue = (Element) nodeListValues.item(i);

			if (!elementValue.getNodeName().matches("(?i)^value$")) {
				continue;
			}

			String attributeType = elementValue.getAttribute("type");
			String type = "";

			if (attributeType.matches("(?i)(str|string)")) {
				type = "str";
			} else if (attributeType.matches("(?i)(num|number|int|integer|float)")) {
				type = "num";
			} else if (attributeType.matches("(?i)(var|variable)")) {
				type = "var";
			} else if (attributeType.matches("(?i)(if)")) {
				type = "if";
			} else if (attributeType.matches("(?i)(pct|percent|percentage)")) {
				type = "pct";
			} else if (attributeType.matches("(?i)(concat)")) {
				type = "concat";
			} else if (attributeType.matches("(?i)(max|maximum)")) {
				type = "max";
			} else if (attributeType.matches("(?i)(min|minimum)")) {
				type = "min";
			} else if (attributeType.matches("(?i)(add)")) {
				type = "add";
			} else if (attributeType.matches("(?i)(sub)")) {
				type = "sub";
			} else if (attributeType.matches("(?i)(mul)")) {
				type = "mul";
			} else if (attributeType.matches("(?i)(div)")) {
				type = "div";
			} else if (attributeType.matches("(?i)(round)")) {
				type = "round";
			} else if (attributeType.matches("(?i)(mod|modulo)")) {
				type = "mod";
			} else if (attributeType.matches("(?i)(imod|intmod|imodulo|intmodulo|modi|modint|moduloi|moduloint)")) {
				type = "modi";
			} else {
				continue;
			}

			String value = elementValue.getTextContent().replaceAll("\\$(?=[0-9a-fk-or])", "\u00a7");

			Value val = new Value(type, value);
			val.values = getValues(elementValue);
			values.add(val);
		}

		return values;
	}

	private String getValue(Value value) {
		if (value.type == "str") {
			return value.value;
		} else if (value.type == "num") {
			return value.value;
		} else if (value.type == "var") {
			return getVariableValue(value.value);
		} else if (value.type == "if" && value.values.size() == 3) {
			try {
				Boolean isTrue = Boolean.parseBoolean(getValue(value.values.get(0)));
				if (isTrue.booleanValue()) {
					return getValue(value.values.get(1));
				}
				return getValue(value.values.get(2));
			} catch (Exception e) {
				return "?";
			}
		} else if (value.type == "pct" && value.values.size() == 2) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				return Double.toString(arg0 / arg1 * 100);
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type == "concat") {
			String str = "";
			for (Value val : value.values) {
				str += getValue(val);
			}
			return str;
		} else if (value.type == "max" && (value.values.size() == 2 || value.values.size() == 4)) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				int shift = value.values.size() - 2;
				return arg0 > arg1 ? getValue(value.values.get(0 + shift)) : getValue(value.values.get(1 + shift));
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type == "min" && (value.values.size() == 2 || value.values.size() == 4)) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				int shift = value.values.size() - 2;
				return arg0 < arg1 ? getValue(value.values.get(0 + shift)) : getValue(value.values.get(1 + shift));
			} catch (Exception e) {
				return "0";
			}
		} else if (value.type == "add" && value.values.size() == 2) {
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
		} else if (value.type == "sub" && value.values.size() == 2) {
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
		} else if (value.type == "mul" && value.values.size() == 2) {
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
		} else if (value.type == "div" && value.values.size() == 2) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				return Double.toString(arg0 / arg1);
			} catch (Exception e2) {
				return "0";
			}
		} else if (value.type == "round" && value.values.size() == 2) {
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
		} else if (value.type == "mod" && value.values.size() == 2) {
			try {
				double arg0 = Double.parseDouble(getValue(value.values.get(0)));
				double arg1 = Double.parseDouble(getValue(value.values.get(1)));
				return Double.toString(Math.round((arg0 % arg1) * 10e6) / 10e6);
			} catch (Exception e2) {
				e2.printStackTrace();
				return "0";
			}
		} else if (value.type == "modi" && value.values.size() == 2) {
			try {
				int arg0 = Integer.parseInt(getValue(value.values.get(0)));
				int arg1 = Integer.parseInt(getValue(value.values.get(1)));
				return Integer.toString(arg0 % arg1);
			} catch (Exception e2) {
				return "0";
			}
		}

		return "";
	}

	private String getVariableValue(String var) {
		try {
			if (var.equalsIgnoreCase("day")) {
				return String.format(Locale.ENGLISH, "%d", this.world.getWorldTime() / 24000);
			} else if (var.equalsIgnoreCase("mctime")) {
				long time = this.world.getWorldTime();
				long hour = (time / 1000 + 6) % 24;
				long minute = (time % 1000) * 60 / 1000;
				return String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);
			} else if (var.equalsIgnoreCase("mctimeh")) {
				long hour = (this.world.getWorldTime() / 1000 + 6) % 24;
				return String.format(Locale.ENGLISH, "%02d", hour);
			} else if (var.equalsIgnoreCase("mctimem")) {
				long minute = (this.world.getWorldTime() % 1000) * 60 / 1000;
				return String.format(Locale.ENGLISH, "%02d", minute);
			} else if (var.equalsIgnoreCase("rltime")) {
				return (new SimpleDateFormat("HH:mm")).format(new Date());
			} else if (var.equalsIgnoreCase("light")) {
				try {
					return Integer.toString(this.world.getChunkFromBlockCoords(this.playerPosition[0], this.playerPosition[2]).getBlockLightValue(this.playerPosition[0] & 15, this.playerPosition[1], this.playerPosition[2] & 15, this.world.calculateSkylightSubtracted(1.0f)));
				} catch (Exception e) {
					return "0";
				}
			} else if (var.equalsIgnoreCase("lightfeet")) {
				try {
					return Integer.toString(this.world.getChunkFromBlockCoords(this.playerPosition[0], this.playerPosition[2]).getBlockLightValue(this.playerPosition[0] & 15, (int) Math.round(this.player.boundingBox.minY), this.playerPosition[2] & 15, this.world.calculateSkylightSubtracted(1.0f)));
				} catch (Exception e) {
					return "0";
				}
			} else if (var.equalsIgnoreCase("lightnosun")) {
				try {
					return Integer.toString(this.world.getChunkFromBlockCoords(this.playerPosition[0], this.playerPosition[2]).getSavedLightValue(EnumSkyBlock.Block, this.playerPosition[0] & 15, this.playerPosition[1], this.playerPosition[2] & 15));
				} catch (Exception e) {
					return "0";
				}
			} else if (var.equalsIgnoreCase("lightnosunfeet")) {
				try {
					return Integer.toString(this.world.getChunkFromBlockCoords(this.playerPosition[0], this.playerPosition[2]).getSavedLightValue(EnumSkyBlock.Block, this.playerPosition[0] & 15, (int) Math.round(this.player.boundingBox.minY), this.playerPosition[2] & 15));
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
				return String.format(Locale.ENGLISH, "%.1f", this.player.posX);
			} else if (var.equalsIgnoreCase("y")) {
				return String.format(Locale.ENGLISH, "%.1f", this.player.posY);
			} else if (var.equalsIgnoreCase("yfeet")) {
				return String.format(Locale.ENGLISH, "%.1f", this.player.boundingBox.minY);
			} else if (var.equalsIgnoreCase("z")) {
				return String.format(Locale.ENGLISH, "%.1f", this.player.posZ);
			} else if (var.equalsIgnoreCase("xi")) {
				return Integer.toString(this.playerPosition[0]);
			} else if (var.equalsIgnoreCase("yi")) {
				return Integer.toString(this.playerPosition[1]);
			} else if (var.equalsIgnoreCase("yfeeti")) {
				return Integer.toString((int) Math.floor(this.player.boundingBox.minY));
			} else if (var.equalsIgnoreCase("zi")) {
				return Integer.toString(this.playerPosition[2]);
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
			} else if (var.equalsIgnoreCase("worldname")) {
				return this.world.getWorldInfo().getWorldName();
			} else if (var.equalsIgnoreCase("worldsize")) {
				return Long.toString(this.world.getWorldInfo().getSizeOnDisk());
			} else if (var.equalsIgnoreCase("worldsizemb")) {
				return String.format(Locale.ENGLISH, "%.1f", this.world.getWorldInfo().getSizeOnDisk() / 1048576.0);
			} else if (var.equalsIgnoreCase("seed")) {
				return Long.toString(this.seed);
			} else if (var.equalsIgnoreCase("difficulty")) {
				switch (this.minecraftClient.gameSettings.difficulty) {
				case 0:
					return "Peaceful";
				case 1:
					return "Easy";
				case 2:
					return "Normal";
				case 3:
					return "Hard";
				}
				return "???";
			} else if (var.equalsIgnoreCase("gamemode")) {
				switch (this.world.getWorldInfo().getGameType()) {
				case SURVIVAL:
					return "Survival";
				case CREATIVE:
					return "Creative";
				case ADVENTURE:
					return "Adventure";
				default:
					return "???";
				}
			} else if (var.equalsIgnoreCase("playerlevel")) {
				return Integer.toString(this.player.experienceLevel);
			} else if (var.equalsIgnoreCase("xpthislevel")) {
				return Integer.toString((int) Math.ceil(this.player.experience * this.player.xpBarCap()));
			} else if (var.equalsIgnoreCase("xpuntilnext")) {
				return Integer.toString((int) Math.floor((1.0 - this.player.experience) * this.player.xpBarCap()));
			} else if (var.equalsIgnoreCase("xpcap")) {
				return Integer.toString(this.player.xpBarCap());
			} else if (var.equalsIgnoreCase("dimension")) {
				switch (this.player.dimension) {
				case -1:
					return "Nether";
				case 0:
					return "Overworld";
				case 1:
					return "The End";
				}
				return "???";
			} else if (var.equalsIgnoreCase("biome")) {
				return this.world.getBiomeGenForCoords(this.playerPosition[0], this.playerPosition[2]).biomeName;
			} else if (var.equalsIgnoreCase("username")) {
				return this.player.username;
			} else if (var.equalsIgnoreCase("texturepack")) {
				return this.minecraftClient.texturePackList.getSelectedTexturePack().getTexturePackFileName();
			} else if (var.equalsIgnoreCase("entitiesrendered")) {
				String str = this.minecraftClient.getEntityDebug();
				return str.substring(str.indexOf(' ') + 1, str.indexOf('/'));
			} else if (var.equalsIgnoreCase("entitiestotal")) {
				String str = this.minecraftClient.getEntityDebug();
				return str.substring(str.indexOf('/') + 1, str.indexOf('.'));
			} else if (var.equalsIgnoreCase("daytime")) {
				return Boolean.toString(this.world.calculateSkylightSubtracted(1.0f) < 4);
			} else if (var.equalsIgnoreCase("raining")) {
				return Boolean.toString(this.world.getRainStrength(1.0f) > 0.2f && this.world.getBiomeGenForCoords(this.playerPosition[0], this.playerPosition[2]).canSpawnLightningBolt());
			} else if (var.equalsIgnoreCase("thundering")) {
				return Boolean.toString(this.world.getWorldInfo().isThundering() && this.world.getBiomeGenForCoords(this.playerPosition[0], this.playerPosition[2]).canSpawnLightningBolt());
			} else if (var.equalsIgnoreCase("slimes")) {
				return Boolean.toString(isSlimeChunk(this.playerPosition[0] >> 4, this.playerPosition[2] >> 4) || this.world.getBiomeGenForCoords(this.playerPosition[0], this.playerPosition[2]).biomeID == BiomeGenBase.swampland.biomeID);
			} else if (var.equalsIgnoreCase("hardcore")) {
				return Boolean.toString(this.world.getWorldInfo().isHardcoreModeEnabled());
			} else if (var.equalsIgnoreCase("equippedname")) {
				ItemStack item = this.player.getCurrentEquippedItem();
				String arrows = item != null && item.itemID == Item.bow.shiftedIndex ? " (" + getArrowsInInventory(this.player) + ")" : "";
				return item != null ? item.getDisplayName() + arrows : "";
			} else if (var.equalsIgnoreCase("equippeddamage")) {
				ItemStack item = this.player.getCurrentEquippedItem();
				return Integer.toString(item != null && item.isItemStackDamageable() ? item.getItemDamage() : 0);
			} else if (var.equalsIgnoreCase("equippeddamageleft")) {
				ItemStack item = this.player.getCurrentEquippedItem();
				return Integer.toString(item != null && item.isItemStackDamageable() ? item.getMaxDamage() + 1 - item.getItemDamage() : 0);
			} else if (var.equalsIgnoreCase("equippedmaxdamage")) {
				ItemStack item = this.player.getCurrentEquippedItem();
				return Integer.toString(item != null && item.isItemStackDamageable() ? item.getMaxDamage() + 1 : 0);
			} else if (var.matches("(helmet|chestplate|leggings|boots)(name|maxdamage|damage|damageleft)")) {
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

				ItemStack item = this.player.inventory.armorItemInSlot(slot);
				if (var.endsWith("name")) {
					return item != null ? item.getDisplayName() : "";
				} else if (var.endsWith("maxdamage")) {
					return Integer.toString(item != null && item.isItemStackDamageable() ? item.getMaxDamage() + 1 : 0);
				} else if (var.endsWith("damage")) {
					return Integer.toString(item != null && item.isItemStackDamageable() ? item.getItemDamage() : 0);
				} else if (var.endsWith("damageleft")) {
					return Integer.toString(item != null && item.isItemStackDamageable() ? item.getMaxDamage() + 1 - item.getItemDamage() : 0);
				}
			} else if (var.matches("potioneffect\\d+")) {
				int index = Integer.parseInt(var.substring(12));
				if (this.potionEffects.length > index) {
					String str = this.strTranslate.translateKey(this.potionEffects[index].getEffectName());
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
					int duration = (this.potionEffects[index]).getDuration() / 20;
					return String.format(Locale.ENGLISH, "%d:%02d", duration / 60, duration % 60);
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
			}
		} catch (Exception e) {
			return "null";
		}

		return "{" + var + "}";
	}

	private int getArrowsInInventory(EntityPlayer entityPlayer) {
		if (entityPlayer.inventory.hasItem(Item.arrow.shiftedIndex)) {
			int count = 0;

			for (int i = 0; ++i < entityPlayer.inventory.mainInventory.length;) {
				if (entityPlayer.inventory.mainInventory[i] != null && entityPlayer.inventory.mainInventory[i].itemID == Item.arrow.shiftedIndex) {
					count += entityPlayer.inventory.mainInventory[i].stackSize;
				}
			}

			return count;
		}
		return 0;
	}

	private boolean isSlimeChunk(int x, int z) {
		return this.seed != 0 ? (new Random(this.seed + x * x * 4987142 + x * 5947611 + z * z * 4392871 + z * 389711 ^ 987234911)).nextInt(10) == 0 : false;
	}

	private void drawLeftAlignedString(FontRenderer fontRenderer, String str, int x, int y, int color) {
		fontRenderer.drawStringWithShadow(str, x, y, color);
	}

	private void drawCenteredString(FontRenderer fontRenderer, String str, int x, int y, int color) {
		fontRenderer.drawStringWithShadow(str, x - fontRenderer.getStringWidth(str.replaceAll("(?i)\u00a7[0-9a-fklmnor]", "")) / 2, y, color);
	}

	private void drawRightAlignedString(FontRenderer fontRenderer, String str, int x, int y, int color) {
		fontRenderer.drawStringWithShadow(str, x - fontRenderer.getStringWidth(str.replaceAll("(?i)\u00a7[0-9a-fklmnor]", "")), y, color);
	}
}
