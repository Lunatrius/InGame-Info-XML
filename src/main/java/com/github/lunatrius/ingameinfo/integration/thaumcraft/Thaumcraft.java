package com.github.lunatrius.ingameinfo.integration.thaumcraft;

import com.github.lunatrius.ingameinfo.integration.Plugin;
import com.github.lunatrius.ingameinfo.integration.thaumcraft.tag.TagThaumcraft;
import com.github.lunatrius.ingameinfo.reference.Names;

@SuppressWarnings("UnusedDeclaration")
public class Thaumcraft extends Plugin {
    @Override
    public String getDependency() {
        return Names.Mods.THAUMCRAFT_MODID;
    }

    @Override
    public String getDependencyName() {
        return Names.Mods.THAUMCRAFT_NAME;
    }

    @Override
    public String getDependencyVersion() {
        return thaumcraft.common.Thaumcraft.VERSION;
    }

    @Override
    public void load() {
        TagThaumcraft.register();
    }
}
