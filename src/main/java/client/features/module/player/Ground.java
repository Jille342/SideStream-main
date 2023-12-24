package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import org.lwjgl.input.Keyboard;

public class Ground extends Module {

    public Ground() {
        super("Ground", Keyboard.KEY_NONE, Category.PLAYER);
    }

    BooleanSetting ground;
    ModeSetting type;

    @Override
    public void init() {
        this.ground = new BooleanSetting("Ground", true);
        this.type = new ModeSetting("Ground", "Pre", "Pre", "Post");
        addSetting(ground, type);
        super.init();
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventMotion) {
            if (type.getMode().equals("Pre") && e.isPre())
                mc.player.onGround = ground.isEnable();
            else if (e.isPost()) mc.player.onGround = ground.isEnable();
        }
        super.onEvent(e);
    }

}
