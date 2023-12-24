package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

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