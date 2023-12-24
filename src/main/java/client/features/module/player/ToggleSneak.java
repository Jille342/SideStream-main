package client.features.module.player;


import client.event.Event;
import client.event.listeners.EventTick;
import client.features.module.Module;
import net.minecraft.client.settings.KeyBinding;

public class ToggleSneak extends Module {

    public ToggleSneak() {
        super("ToggleSneak", 0, Category.PLAYER);
    }



    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventTick) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);

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
