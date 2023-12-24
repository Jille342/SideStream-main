package client.ui.element.elements;

import client.ui.element.Panel;
import client.utils.render.ColorUtils;
import client.utils.render.RenderUtils;
import client.utils.render.easing.Color;
import client.utils.render.easing.Value;
import client.ui.element.ElementManager;
import net.minecraft.client.gui.FontRenderer;

public class TextPanel extends Panel {
	
	public String text;
	public FontRenderer fontRenderer;
	public Color color;
	public Value alpha;
	
	public TextPanel(ElementManager elementManager, Value x, Value y, Value w, Value h, boolean isCollidable, FontRenderer fontRenderer, String text, Color color) {
		super(elementManager, x, y, w, h, isCollidable);
		this.text = text;
		this.fontRenderer = fontRenderer;
		this.color = color;
		this.alpha = new Value(255, null);
		addValues(this.alpha);
	}

	public TextPanel(ElementManager elementManager, double x, double y, double w, double h, boolean isCollidable, FontRenderer fontRenderer, String text, Color color) {
		this(elementManager, new Value(x, null), new Value(y, null), new Value(w, null), new Value(h, null), isCollidable, fontRenderer, text, color);
	}

	public void setAlpha(int alpha) {
		this.alpha.value = alpha;
	}

	public int getAlpha() {
		return (int) alpha.value;
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		RenderUtils.glColor(ColorUtils.alpha(color.getColor(), (int)alpha.value));
		fontRenderer.drawString(text, (int)x.getValue(), (int)y.getValue(), 0x40ffffff);
		super.draw(mouseX, mouseY, partialTicks);
	}
}
