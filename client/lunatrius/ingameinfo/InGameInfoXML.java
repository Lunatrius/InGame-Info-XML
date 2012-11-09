package lunatrius.ingameinfo;

import java.io.File;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiChat;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.TickRegistry;

@Mod(modid = "InGameInfoXML")
public class InGameInfoXML {
	@Instance("InGameInfoXML")
	public static InGameInfoXML instance;

	private final InGameInfoCore core = InGameInfoCore.instance();
	private Minecraft minecraftClient = null;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		this.core.init(new File(event.getModConfigurationDirectory(), "InGameInfo.xml"));
		this.core.loadConfig();
	}

	@Init
	public void init(FMLInitializationEvent event) {
		TickRegistry.registerTickHandler(new Ticker(EnumSet.of(TickType.CLIENT, TickType.RENDER)), Side.CLIENT);

		this.core.setLogger(FMLCommonHandler.instance().getFMLLogger());
		this.core.setClient(this.minecraftClient = Minecraft.getMinecraft());
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		this.core.setServer(event.getServer());
	}

	@ServerStopping
	public void serverStopping(FMLServerStoppingEvent event) {
		this.core.setServer(null);
	}

	public boolean onTick(TickType tick, boolean start) {
		if (start) {
			return true;
		}

		if (this.minecraftClient != null && this.minecraftClient.gameSettings != null && !this.minecraftClient.gameSettings.showDebugInfo) {
			if (this.minecraftClient.currentScreen == null || this.minecraftClient.currentScreen instanceof GuiChat) {
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
