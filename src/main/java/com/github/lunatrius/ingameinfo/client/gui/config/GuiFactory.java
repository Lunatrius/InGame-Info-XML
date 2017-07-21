package com.github.lunatrius.ingameinfo.client.gui.config;

import com.github.lunatrius.core.client.gui.config.GuiConfigComplex;
import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import com.github.lunatrius.ingameinfo.reference.Names;
import com.github.lunatrius.ingameinfo.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class GuiFactory implements IModGuiFactory {
    @Override
    public void initialize(final Minecraft minecraftInstance) {
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public static class GuiModConfig extends GuiConfigComplex {
        public GuiModConfig(final GuiScreen guiScreen) {
            super(guiScreen, Reference.MODID, ConfigurationHandler.configuration, Names.Config.LANG_PREFIX);
        }
    }

	@Override
	public GuiScreen createConfigGui(GuiScreen arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		// TODO Auto-generated method stub
		return false;
	}
}