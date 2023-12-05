package com.github.satellite.features.module.render;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.Colors;
import com.github.satellite.utils.RenderingUtils;
import com.github.satellite.utils.Translate;
import com.github.satellite.utils.font.CFontRenderer;
import com.github.satellite.utils.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class HUD2 extends Module {
    public static BooleanSetting background;
    public static BooleanSetting info;
    public static BooleanSetting OUTLINE;
    public static BooleanSetting inversion;
    public static ModeSetting colormode;
    public static ModeSetting namecolormode;

    public static double lastPosX = Double.NaN;
    public static double lastPosZ = Double.NaN;
      public static ArrayList<Double> distances = new ArrayList<Double>();

    public HUD2() {
        super("HUD2", Keyboard.KEY_NONE, Category.RENDER);

    }

    @Override
    public void init() {
        super.init();
        colormode = new ModeSetting("Color Mode ", "Pulsing", new String[]{"Default", "Rainbow", "Pulsing", "Category", "Test"});
        namecolormode = new ModeSetting("Name Color Mode ", "Default", new String[]{"Default", "Rainbow", "Pulsing", "Category", "Test"});
        background = new BooleanSetting("BackGround", true);
        info = new BooleanSetting("Info", true);
        OUTLINE = new BooleanSetting("Outline", true);
        inversion = new BooleanSetting("Outline", true);
        addSetting(info, OUTLINE, background, colormode, inversion, namecolormode);
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosZ)) {
                double differenceX = Math.abs(lastPosX - mc.player.posX);
                double differenceZ = Math.abs(lastPosZ - mc.player.posZ);
                double distance = Math.sqrt(differenceX * differenceX + differenceZ * differenceZ) * 2;

                distances.add(distance);
                if (distances.size() > 20)
                    distances.remove(0);
            }

            lastPosX = mc.player.posX;
            lastPosZ = mc.player.posZ;

        }
    }
}