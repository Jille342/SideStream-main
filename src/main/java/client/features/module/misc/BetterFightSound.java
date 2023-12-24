package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import net.minecraft.init.SoundEvents;
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
                  if(  ((SPacketSoundEffect) p).getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP)
                    event.setCancelled(true);
                    if(  ((SPacketSoundEffect) p).getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_WEAK)
                        event.setCancelled(true);
                    if(  ((SPacketSoundEffect) p).getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_STRONG)
                        event.setCancelled(true);
                    if(  ((SPacketSoundEffect) p).getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE)
                        event.setCancelled(true);
                    if(  ((SPacketSoundEffect) p).getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_CRIT)
                        event.setCancelled(true);
                    if(  ((SPacketSoundEffect) p).getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK)
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
