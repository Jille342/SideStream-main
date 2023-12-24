package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;

public class Fullbright extends Module {
	public Fullbright() {
		super("Fullbright", 0,	Category.RENDER);
	}

	float lastGamma;

    @Override
    public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
    		mc.gameSettings.gammaSetting = 10E+3F;
    	}
    	super.onEvent(e);
    }

	@Override
	public void onEnable() {
		lastGamma = mc.gameSettings.gammaSetting;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		mc.gameSettings.gammaSetting = lastGamma;
		super.onDisable();
	}
}
