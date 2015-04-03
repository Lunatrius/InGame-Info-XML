package com.github.lunatrius.ingameinfo.integration.terrafirmacraft;

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
        return com.bioxx.tfc.Reference.ModVersion;
    }

    @Override
    public void load() {
        TagTerraFirmaCraft.register();
    }
}
