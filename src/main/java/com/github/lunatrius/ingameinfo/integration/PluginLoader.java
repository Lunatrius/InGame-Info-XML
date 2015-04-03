package com.github.lunatrius.ingameinfo.integration;

import com.github.lunatrius.ingameinfo.reference.Reference;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;

public class PluginLoader {
    private final List<Plugin> plugins = new ArrayList<Plugin>();

    private static PluginLoader instance = null;

    public static PluginLoader getInstance() {
        if (instance == null) {
            instance = new PluginLoader();
        }

        return instance;
    }

    public void preInit(final FMLPreInitializationEvent event) {
        final String[] names = new String[] {
                "com.github.lunatrius.ingameinfo.integration.bloodmagic.BloodMagic",
                "com.github.lunatrius.ingameinfo.integration.simplyjetpacks.SimplyJetpacks",
                "com.github.lunatrius.ingameinfo.integration.terrafirmacraft.TerraFirmaCraft",
                "com.github.lunatrius.ingameinfo.integration.thaumcraft.Thaumcraft"
        };

        for (final String name : names) {
            try {
                final Class<?> clazz = Class.forName(name);

                if (!Plugin.class.isAssignableFrom(clazz)) {
                    continue;
                }

                final Class<? extends Plugin> clazzPlugin = (Class<? extends Plugin>) clazz;
                final Plugin plugin = clazzPlugin.newInstance();
                if (plugin.canLoad()) {
                    this.plugins.add(plugin);
                }
            } catch (final ClassNotFoundException cnfe) {
                Reference.logger.error("{} not found, skipping...", name);
            } catch (final InstantiationException ie) {
                Reference.logger.error("{} could not be created, skipping...", name);
            } catch (final IllegalAccessException iae) {
                Reference.logger.error("{} could not be accessed, skipping...", name);
            }
        }

        if (this.plugins.size() > 0) {
            event.getModMetadata().description += "\nCompiled against " + this.plugins;
        }
    }

    public void postInit(final FMLPostInitializationEvent event) {
        for (final Plugin plugin : this.plugins) {
            plugin.load();

            Reference.logger.debug("Loaded {} integration.", plugin.getDependencyName());
        }

        // all plugins loaded, let it go~
        instance = null;
    }
}
