package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;

public class SuccubusChecker extends Module {
    public SuccubusChecker(){
        super("SuccubusChecker", 0, Module.Category.MISC);

    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                Packet<?> p = event.getPacket();

                if(p instanceof SPacketUpdateScore) {
                   if( ((SPacketUpdateScore) p).getScoreValue()<6) {
                       ChatUtils.printChat("Can be Saccubus");
                   }

                }
                //      if(p instanceof SPacketExplosion) {
                //        event.setCancelled(true);
                //  }
            }
        }
        super.onEvent(e);
    }
}
