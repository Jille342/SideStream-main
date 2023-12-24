package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventRenderWorld;
import client.features.module.Module;
import client.utils.render.RenderUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ESP extends Module {

	public ESP() {
		super("ESP", Keyboard.KEY_NONE,	Category.RENDER);
	}

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventRenderWorld) {;
			for(Entity entity : mc.world.loadedEntityList) {
				if(!(entity instanceof EntityLivingBase))
					continue;
				if(!(entity == mc.player && mc.gameSettings.thirdPersonView==0)) {
					RenderUtils.drawEntityBox(entity, new Color(255, 255, 255, 0x40));
				}
			}
		}
		super.onEvent(e);
	}
}
