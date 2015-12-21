package com.github.lunatrius.ingameinfo.handler;

import com.github.lunatrius.ingameinfo.reference.Names;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {
    public static final KeyInputHandler INSTANCE = new KeyInputHandler();

    private static final KeyBinding KEY_BINDING_TOGGLE = new KeyBinding(Names.Keys.TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);

    public static final KeyBinding[] KEY_BINDINGS = new KeyBinding[] {
            KEY_BINDING_TOGGLE
    };

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private KeyInputHandler() {}

    @SubscribeEvent
    public void onKeyInput(final InputEvent event) {
        if (this.minecraft.currentScreen == null) {
            if (KEY_BINDING_TOGGLE.isPressed()) {
                Ticker.enabled = !Ticker.enabled;
            }
        }
    }
}
