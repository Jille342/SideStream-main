package client.ui;

import client.event.listeners.EventRenderGUI;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.ui.element.ElementManager;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import client.utils.render.ColorUtils;
import client.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;

public class HUD {

	protected Minecraft mc = Minecraft.getMinecraft();

	private final CFontRenderer font = Fonts.default18;
	public ElementManager gui;

	public HUD() {
		this.gui = new ElementManager();
	}

	public static class ModuleComparator implements Comparator<Module> {

		@Override
		public int compare(Module o1, Module o2) {
			if(Minecraft.getMinecraft().fontRenderer.getStringWidth(o1.getDisplayName()) > Minecraft.getMinecraft().fontRenderer.getStringWidth(o2.getDisplayName()))
				return -1;
			if(Minecraft.getMinecraft().fontRenderer.getStringWidth(o1.getDisplayName()) < Minecraft.getMinecraft().fontRenderer.getStringWidth(o2.getDisplayName()))
				return 1;
			return 0;
		}

	}

	public void draw() {
		final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		int i1 = scaledresolution.getScaledWidth();
		int j1 = scaledresolution.getScaledHeight();
		final int k1 = Mouse.getX() * i1 / this.mc.displayWidth;
		final int l1 = j1 - Mouse.getY() * j1 / this.mc.displayHeight - 1;

		Display.setResizable(true);
		EventRenderGUI e = new EventRenderGUI();
		Client.onEvent(e);

		GL11.glPushMatrix();

		float hue = (System.currentTimeMillis() % 4000) / 4000f;

		ScaledResolution sr = new ScaledResolution(mc);

		FontRenderer fr = mc.fontRenderer;

		Collections.sort(ModuleManager.modules, new ModuleComparator());

		int i=0;
		for(Module m : ModuleManager.modules) {
			if(!m.isEnable())
				continue;

			int offset = (fr.FONT_HEIGHT + 4) * i;

			String name = m.getDisplayName();

			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(name) - 8, offset, sr.getScaledWidth(), offset + fr.FONT_HEIGHT + 4, 0x90000000);
			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(name) - 6, offset, sr.getScaledWidth() - fr.getStringWidth(m.getDisplayName()) - 8, offset + fr.FONT_HEIGHT + 4, ColorUtils.HSBtoRGB((float) (hue+(offset*0.00314159265359)), 0.8F, 0.97F));
			font.drawStringWithShadow(name, sr.getScaledWidth() - fr.getStringWidth(name) - 3.5F, offset + 2.5F, 0xffffffff);

			i++;
		}

		String name = Client.NAME;
		name = name.substring(0, 1).replaceAll(name.substring(0, 1), "\247c" + name.substring(0, 1)) + name.substring(1).replaceAll(name.substring(1), "\247f" + name.substring(1));
		font.drawStringWithShadow(name, 3, 4, new Color(255, 50, 50).getRGB());

		GL11.glPopMatrix();

		if (gui != null) {
			gui.updateCollision(k1, l1);
			gui.draw(k1, l1, this.mc.getTickLength());
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
