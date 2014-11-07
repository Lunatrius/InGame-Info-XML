package com.github.lunatrius.ingameinfo.integration.thaumcraft;

import com.github.lunatrius.ingameinfo.integration.thaumcraft.tag.TagThaumcraft;
import com.github.lunatrius.ingameinfo.reference.Names;
import com.github.lunatrius.ingameinfo.reference.Reference;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = Thaumcraft.MODID, name = Thaumcraft.NAME, version = Reference.VERSION, dependencies = Thaumcraft.DEPENDENCIES)
public class Thaumcraft {
    public static final String MODID = Reference.MODID + "|" + Names.Mods.THAUMCRAFT_MODID + "Integration";
    public static final String NAME = Reference.NAME + " - " + Names.Mods.THAUMCRAFT_NAME + " Integration";
    public static final String DEPENDENCIES = "after:" + Reference.MODID + ";after:" + Names.Mods.THAUMCRAFT_MODID;

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded(Names.Mods.THAUMCRAFT_MODID)) {
            TagThaumcraft.register();
        }
    }
}
