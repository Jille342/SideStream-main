package client.features.module.render;

import client.features.module.Module;
import org.lwjgl.input.Keyboard;

public class HUD extends Module {
    public HUD() {

        super("HUD", Keyboard.KEY_NONE,	Category.RENDER);
    }
}

