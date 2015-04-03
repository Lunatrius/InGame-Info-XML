package com.github.lunatrius.ingameinfo.integration.bloodmagic;

import com.github.lunatrius.ingameinfo.integration.Plugin;
import com.github.lunatrius.ingameinfo.integration.bloodmagic.tag.TagBloodMagic;
import com.github.lunatrius.ingameinfo.reference.Names;

@SuppressWarnings("UnusedDeclaration")
public class BloodMagic extends Plugin {
    @Override
    public String getDependency() {
        return Names.Mods.BLOODMAGIC_MODID;
    }

    @Override
    public String getDependencyName() {
        return Names.Mods.BLOODMAGIC_NAME;
    }

    @Override
    public void load() {
        TagBloodMagic.register();
    }
}
