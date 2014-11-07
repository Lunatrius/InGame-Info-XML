package com.github.lunatrius.ingameinfo.integration.simplyjetpacks;

import com.github.lunatrius.ingameinfo.integration.simplyjetpacks.tag.TagSimplyJetpacks;
import com.github.lunatrius.ingameinfo.reference.Names;
import com.github.lunatrius.ingameinfo.reference.Reference;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = SimplyJetpacks.MODID, name = SimplyJetpacks.NAME, version = Reference.VERSION, dependencies = SimplyJetpacks.DEPENDENCIES)
public class SimplyJetpacks {
    public static final String MODID = Reference.MODID + "|" + Names.Mods.SIMPLYJETPACKS_MODID + "Integration";
    public static final String NAME = Reference.NAME + " - " + Names.Mods.SIMPLYJETPACKS_NAME + " Integration";
    public static final String DEPENDENCIES = "after:" + Reference.MODID + ";after:" + Names.Mods.SIMPLYJETPACKS_MODID;

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded(Names.Mods.SIMPLYJETPACKS_MODID)) {
            TagSimplyJetpacks.register();
        }
    }
}
