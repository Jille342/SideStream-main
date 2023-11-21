package com.github.satellite.features.module.misc;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSoundEffect;
import org.lwjgl.input.Keyboard;

public class BetterFightSound extends Module {

    public BetterFightSound() {
        super("BetterFightSound", Keyboard.KEY_NONE, Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                Packet<?> p = event.getPacket();

                if(p instanceof SPacketSoundEffect) {
                    event.setCancelled(true);
                }
                //      if(p instanceof SPacketExplosion) {
                //        event.setCancelled(true);
                //  }
            }
        }
        super.onEvent(e);
    }

}
