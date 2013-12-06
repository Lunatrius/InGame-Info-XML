package lunatrius.ingameinfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.io.File;
import java.util.EnumSet;
import java.util.logging.Logger;

@Mod(modid = "InGameInfoXML")
public class InGameInfoXML {
	@Instance("InGameInfoXML")
	public static InGameInfoXML instance;
	public static final Logger LOGGER = FMLCommonHandler.instance().getFMLLogger();

	private final InGameInfoCore core = InGameInfoCore.instance();
	private Minecraft minecraftClient = null;
	private boolean showInChat;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		File configurationFile = event.getSuggestedConfigurationFile();
		Configuration configuration = new Configuration(configurationFile);

		configuration.load();
		Property configType = configuration.get(Configuration.CATEGORY_GENERAL, "type", "xml", "Configuration type (xml or text). The default file that will be read will be InGameInfo.xml or InGameInfo.txt.");
		Property showInChat = configuration.get(Configuration.CATEGORY_GENERAL, "showInChat", true, "Displays the overlay in chat.");
		this.showInChat = showInChat.getBoolean(true);
		configuration.save();

		this.core.init(event.getModConfigurationDirectory(), configType.getString());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		TickRegistry.registerTickHandler(new Ticker(EnumSet.of(TickType.CLIENT, TickType.RENDER)), Side.CLIENT);

		this.core.setClient(this.minecraftClient = Minecraft.getMinecraft());
		this.core.copyDefaultConfig();
		this.core.reloadConfig();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		this.core.setServer(event.getServer());
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		this.core.setServer(null);
	}

	public boolean onTick(TickType tick, boolean start) {
		if (start) {
			return true;
		}

		if (this.minecraftClient != null && this.minecraftClient.gameSettings != null && !this.minecraftClient.gameSettings.showDebugInfo) {
			if (this.minecraftClient.currentScreen == null || this.showInChat && this.minecraftClient.currentScreen instanceof GuiChat) {
				if (tick == TickType.CLIENT) {
					this.core.onTickClient();
				} else if (tick == TickType.RENDER) {
					this.core.onTickRender();
				}
			}
		}

		return true;
	}
}
