package com.github.lunatrius.ingameinfo.handler;


import com.github.lunatrius.ingameinfo.reference.Names;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import static cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyInputHandler {
    public static final KeyInputHandler INSTANCE = new KeyInputHandler();

    private static final KeyBinding KEY_BINDING_TOGGLE = new KeyBinding(Names.Keys.TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);

    public static final KeyBinding[] KEY_BINDINGS = new KeyBinding[] {
            KEY_BINDING_TOGGLE
    };

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private KeyInputHandler() {}

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        for (KeyBinding keyBinding : KEY_BINDINGS) {
            if (keyBinding.isPressed()) {
                if (this.minecraft.currentScreen == null) {
                    Ticker.enabled = !Ticker.enabled;
                }
            }
        }
    }
}
