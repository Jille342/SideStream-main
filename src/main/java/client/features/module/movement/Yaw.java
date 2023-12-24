package client.features.module.movement;

import client.event.Event;
import client.event.listeners.EventPlayerInput;
import client.features.module.Module;
import org.lwjgl.input.Keyboard;

public class Yaw extends Module {
	
	float yaw = 0;
	
	public Yaw() {
		super("Yaw", Keyboard.KEY_NUMPAD4, Category.MOVEMENT);
	}
	
	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventPlayerInput) {
			mc.player.rotationYaw += ((float) ( (Math.floor( ( mc.player.rotationYaw + 22.5) / 45 ) * 45)) - mc.player.rotationYaw) / 3;
			super.onEvent(e);
		}
	}
}
