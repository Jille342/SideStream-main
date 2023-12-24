package client.features.module.render;

import client.Client;
import client.event.Event;
import client.event.listeners.EventRenderGUI;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.ui.gui.clickGUI.GuiClickGUI;
import client.ui.theme.Theme;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Module {

	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT,	Category.RENDER);
	}

	public ModeSetting theme;
	public static BooleanSetting autoGuiScale;

	@Override
	public void init() {
		List<String> mode = new ArrayList<String>();
		for(Theme theme : Client.themeManager.themes) {
			mode.add(theme.getName());
		}
		String[] modes = mode.toArray(new String[mode.size()]);
		this.theme = new ModeSetting("Theme", mode.get(0), modes);
		this.autoGuiScale = new BooleanSetting("AutoResize", true);
		addSetting(theme, autoGuiScale);
		super.init();
	}

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventRenderGUI) {
			Client.themeManager.setTheme(theme.getMode());
			mc.displayGuiScreen(new GuiClickGUI(0));
		}
		super.onEvent(e);
	}

	@Override
	public void onEnable() {
		mc.displayGuiScreen(new GuiClickGUI(0));
		super.onEnable();
	}
}
