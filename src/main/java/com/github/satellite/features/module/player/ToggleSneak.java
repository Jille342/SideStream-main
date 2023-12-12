package com.github.satellite.features.module.player;


import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.KeyBindSetting;
import com.github.satellite.ui.gui.clickGUI.GuiClickGUI;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ToggleSneak extends Module {

    public ToggleSneak() {
        super("ToggleSneak", 0, Category.PLAYER);
    }



    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            mc.player.setSneaking(true);

            super.onEvent(e);
        }
    }

    @Override
    public void onDisable() {
       if (mc.player.isSneaking()){
           mc.player.setSneaking(false);
       }

        super.onDisable();
    }

}
