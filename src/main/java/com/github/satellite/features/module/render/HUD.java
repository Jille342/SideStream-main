package com.github.satellite.features.module.render;

import com.github.satellite.features.module.Module;
import org.lwjgl.input.Keyboard;

public class HUD extends Module {
    public HUD() {

        super("HUD", Keyboard.KEY_NONE,	Category.RENDER);
    }
}

