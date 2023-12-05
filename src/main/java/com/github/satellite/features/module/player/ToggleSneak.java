package com.github.satellite.features.module.player;


import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.KeyBindSetting;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ToggleSneak extends Module {

    public ToggleSneak() {
        super("ToggleSneak", 0, Category.PLAYER);
    }

    KeyBindSetting MouseMoveKey;



    int lastMouseX = Mouse.getX();
    int lastMouseY = Mouse.getY();

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if(mc.currentScreen!=null) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            }
        }
        super.onEvent(e);
    }

}
