package com.github.lunatrius.ingameinfo.integration.terrafirmacraft;

import com.bioxx.tfc.Reference;
import com.github.lunatrius.ingameinfo.integration.Plugin;
import com.github.lunatrius.ingameinfo.integration.terrafirmacraft.tag.TagTerraFirmaCraft;
import com.github.lunatrius.ingameinfo.reference.Names;

// NOTE: requires VM arguments: -Dfml.coreMods.load=com.bioxx.tfc.TFCASMLoadingPlugin
@SuppressWarnings("UnusedDeclaration")
public class TerraFirmaCraft extends Plugin {
    @Override
    public String getDependency() {
        return Names.Mods.TERRAFIRMACRAFT_MODID;
    }

    @Override
    public String getDependencyName() {
        return Names.Mods.TERRAFIRMACRAFT_NAME;
    }

    @Override
    public String getDependencyVersion() {
        return Reference.MOD_VERSION;
    }

    @Override
    public void load() {
        TagTerraFirmaCraft.register();
    }
}
