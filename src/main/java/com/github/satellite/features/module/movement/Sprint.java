package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.features.module.Module;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import javax.swing.text.JTextComponent;

public class Sprint extends Module {
    public Sprint () {
        super("Sprint", Keyboard.KEY_NONE, Category.MOVEMENT);
    }
    public void onEvent(Event<?> e) {
        if(e.isPre()) {
            if(mc.gameSettings.keyBindForward.isKeyDown() && !(mc.player.getItemInUseCount() > 0))
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

}
