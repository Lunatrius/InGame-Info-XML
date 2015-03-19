package com.github.lunatrius.ingameinfo.integration.bloodmagic;

import com.github.lunatrius.ingameinfo.integration.bloodmagic.tag.TagBloodMagic;
import com.github.lunatrius.ingameinfo.reference.Names;
import com.github.lunatrius.ingameinfo.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = BloodMagic.MODID, name = BloodMagic.NAME, version = Reference.VERSION, dependencies = BloodMagic.DEPENDENCIES)
public class BloodMagic {
    public static final String MODID = Reference.MODID + "|" + Names.Mods.BLOODMAGIC_MODID + "Integration";
    public static final String NAME = Reference.NAME + " - " + Names.Mods.BLOODMAGIC_NAME + " Integration";
    public static final String DEPENDENCIES = "after:" + Reference.MODID + ";after:" + Names.Mods.BLOODMAGIC_MODID;

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT) {
            return;
        }

        if (Loader.isModLoaded(Names.Mods.BLOODMAGIC_MODID)) {
            TagBloodMagic.register();
        }
    }
}
