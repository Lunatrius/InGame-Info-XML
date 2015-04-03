package com.github.lunatrius.ingameinfo.integration.simplyjetpacks;

import com.github.lunatrius.ingameinfo.integration.Plugin;
import com.github.lunatrius.ingameinfo.integration.simplyjetpacks.tag.TagSimplyJetpacks;
import com.github.lunatrius.ingameinfo.reference.Names;

@SuppressWarnings("UnusedDeclaration")
public class SimplyJetpacks extends Plugin {
    @Override
    public String getDependency() {
        return Names.Mods.SIMPLYJETPACKS_MODID;
    }

    @Override
    public String getDependencyName() {
        return Names.Mods.SIMPLYJETPACKS_NAME;
    }

    @Override
    public void load() {
        TagSimplyJetpacks.register();
    }
}
