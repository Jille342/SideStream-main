package client.features.module.movement;

import client.event.Event;
import client.features.module.Module;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

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
