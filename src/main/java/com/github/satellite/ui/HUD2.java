package com.github.satellite.ui;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.ui.element.ElementManager;
import com.github.satellite.utils.Colors;
import com.github.satellite.utils.RenderingUtils;
import com.github.satellite.utils.Translate;
import com.github.satellite.utils.font.CFontRenderer;
import com.github.satellite.utils.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.github.satellite.features.module.render.HUD2.*;

public class HUD2 {

    protected Minecraft mc = Minecraft.getMinecraft();

    public ElementManager gui;

    private final CFontRenderer font = Fonts.default18;
    private final CFontRenderer font2 = Fonts.default20;
    private final CFontRenderer font3 = Fonts.elliot18;

    public HUD2() {
        this.gui = new ElementManager();
    }

    public void draw() {
        int[] counter = {1};
        int color = -1;
        switch (namecolormode.getMode()) {
            case "Default":
                color = new Color(50, 200, 255).getRGB();
                break;
            case "Rainbow":
                color = Colors.rainbow((counter[0] * 15) * 7, 0.8f, 1.0f);
                break;
            case "Pulsing":
                color = TwoColoreffect(new Color(50, 200, 255), new Color(9, 9, 79), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                break;
            case "Test":
                color = TwoColoreffect(new Color(65, 179, 255), new Color(248, 54, 255), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                break;
        }
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float height = 10;
        String name = Satellite.NAME;
        String fps = "FPS: \2477" + mc.getDebugFPS();
        String ping = "PING: \2477" + ((mc.getCurrentServerData() != null) ? mc.getCurrentServerData().pingToServer : 0);
        String coord = "XYZ: \2477" + MathHelper.floor(this.mc.player.posX) + " / " + MathHelper.floor(this.mc.player.posY) + " / " + MathHelper.floor(this.mc.player.posZ);String
                build = "Build: \2477" + Satellite.VERSION;
      //  name = name.substring(0, 1).replaceAll(name.substring(0, 1), "\247c" + name.substring(0, 1)) + name.substring(1).replaceAll(name.substring(1), "\247f" + name.substring(1));
        name = name.substring(0, 1).replaceAll(name.substring(0, 1),  name.substring(0, 1)) + name.substring(1).replaceAll(name.substring(1), "\247f" + name.substring(1));

        if (!mc.gameSettings.showDebugInfo) {
            font2.drawStringWithShadow(name, 3, 4, color);
            if (mc.currentScreen instanceof GuiChat) {
                height += 14;
            }
            if (info.isEnable()) {
                font.drawStringWithShadow(fps, 3, scaledResolution.getScaledHeight() - height, -1);
                font.drawStringWithShadow(coord, font.getStringWidth(fps) + 6, scaledResolution.getScaledHeight() - height, -1);
                font.drawStringWithShadow(ping, 3, scaledResolution.getScaledHeight() - height - font.getHeight() - 2, -1);
            }
            font.drawStringWithShadow(build, 5, 16, -1);

            this.drawGaeHud();
        }

    }

    private void drawGaeHud() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
        ArrayList<Module> sortedList = getSortedModules(font3);
        int listOffset = 10, y = 1;
        int[] counter = {1};

        GL11.glEnable(3042);
        for (int i = 0, sortedListSize = sortedList.size(); i < sortedListSize; i++) {
            Module module = sortedList.get(i);
            Translate translate = module.getTranslate();

            String moduleLabel = module.getDisplayName();
            float length = font3.getStringWidth(moduleLabel);
            float featureX = width - length - 3.0F;
            boolean enable = module.isEnable();
            if (enable) {
                translate.interpolate(featureX, y, 7);
            } else {
                translate.interpolate(width + 3, y, 7);
            }
            double translateX = translate.getX();
            double translateY = translate.getY();
            boolean visible = ((translateX > -listOffset));
            if (visible) {
                int color = -1;
                switch (colormode.getMode()) {
                    case "Default":
                        color = new Color(50   , 100, 255).getRGB();
                        break;
                    case "Rainbow":
                        color = Colors.rainbow((counter[0] * 15) * 7, 0.8f, 1.0f);
                        break;
                    case "Pulsing":
                        color = TwoColoreffect(new Color(50, 200, 255), new Color(9, 9, 79), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                        break;
                    case "Test":
                        color = TwoColoreffect(new Color(65, 179, 255), new Color(248, 54, 255), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                        break;
                }
                int nextIndex = sortedList.indexOf(module) + 1;
                Module nextModule = null;
                if (sortedList.size() > nextIndex)
                    nextModule = getNextEnabledModule(sortedList, nextIndex);
                if ((Boolean) background.enable)
                    RenderingUtils.drawRect(translateX - 2.0D, translateY - 1.0D, width, translateY + listOffset - 1.0D, 0x90000000);
                if ((Boolean) OUTLINE.enable) {
                    RenderingUtils.drawRect(translateX - 2.6D, translateY - 1.0D, translateX - 2.0D, translateY + listOffset - 1.0D, color);
                    double offsetY = listOffset;
                    if (nextModule != null) {
                        double dif = (length - font3.getStringWidth(nextModule.getDisplayName()));
                        RenderingUtils.drawRect(translateX - 2.6D, translateY + offsetY - 1.0D, translateX - 2.6D + dif, translateY + offsetY - 0.5D, color);
                    } else {
                        RenderingUtils.drawRect(translateX - 2.6D, translateY + offsetY - 1.0D, width, translateY + offsetY - 0.6D, color);
                    }
                }

                font3.drawStringWithShadow(moduleLabel, (float) translateX, (float) translateY + 2, color);

                if (module.isEnable()) {
                    y += listOffset;
                    counter[0] -= 1F;
                }
            }
        }
    }

    private Module getNextEnabledModule(ArrayList<Module> modules, int startingIndex) {
        for (int i = startingIndex, modulesSize = modules.size(); i < modulesSize; i++) {
            Module module = modules.get(i);
            if (module.isEnable())
                return module;
        }
        return null;
    }
    private ArrayList<Module> getSortedModules(CFontRenderer fr) {
        ArrayList<Module> sortedList = new ArrayList<>(ModuleManager.modules);
        sortedList.sort(Comparator.comparingDouble(e -> -fr.getStringWidth(e.getDisplayName())));
        return sortedList;
    }
    public static Color TwoColoreffect(final Color color, final Color color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }
    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + index / count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }
    public double getDistTraveled() {
        double total = 0;
        for (double d : distances) {
            total += d;
        }
        return total;
    }
}
