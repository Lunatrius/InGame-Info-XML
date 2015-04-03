package com.github.lunatrius.ingameinfo.integration;

import net.minecraftforge.fml.common.Loader;

public abstract class Plugin {
    protected abstract String getDependency();

    public String getDependencyName() {
        return getDependency();
    }

    public String getDependencyVersion() {
        return "";
    }

    public boolean canLoad() {
        return Loader.isModLoaded(getDependency());
    }

    public abstract void load();

    @Override
    public String toString() {
        final String dependencyVersion = getDependencyVersion();
        if (dependencyVersion != null && !dependencyVersion.isEmpty()) {
            return getDependencyName() + " " + dependencyVersion;
        }

        return getDependencyName();
    }
}
