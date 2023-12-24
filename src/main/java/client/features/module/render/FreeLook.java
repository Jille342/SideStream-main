package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventRenderWorld;
import client.features.module.Module;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class FreeLook extends Module {

    public FreeLook() {
        super("FreeLook", Keyboard.KEY_U, Category.RENDER);
    }

    float yaw, pitch;
    float lastYaw, lastPitch;

    @Override
    public void onEnable() {
        this.yaw = mc.player.rotationYaw;
        this.pitch = mc.player.rotationPitch;
        this.lastYaw = mc.player.rotationYaw;
        this.lastPitch = mc.player.rotationPitch;
        super.onEnable();
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventRenderWorld) {
            if (mc.currentScreen == null) {
                this.yaw -= mc.player.rotationYaw - lastYaw;
                this.pitch -= mc.player.rotationPitch - lastPitch;
            }
            GL11.glRotated(-pitch / 180, 0.0D, 0.0D, 0.0D);
            GL11.glRotated(-yaw / 180, 0.0D, 1.0D, 0.0D);
            mc.player.rotationYaw = lastYaw;
            mc.player.rotationPitch = lastPitch;
            this.lastYaw = mc.player.rotationYaw;
            this.lastPitch = mc.player.rotationPitch;
        }
        super.onEvent(e);
    }
}
