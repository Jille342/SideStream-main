package com.github.satellite.features.module.misc;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.ChatUtils;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import org.lwjgl.input.Keyboard;

public class NameProtect extends Module {

    public NameProtect() {
        super("NameProtect", Keyboard.KEY_NONE, Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                Packet<?> p = event.getPacket();

                if(p instanceof SPacketChat) {

                    SPacketChat packet = (SPacketChat) event.getPacket();
                    if (packet.getChatComponent().getUnformattedText().contains(mc.player.getName())) {
                        String temp = packet.getChatComponent().getFormattedText();
                        ChatUtils.printChat(temp.replaceAll(mc.player.getName(), "\247d" + Satellite.NAME + "User" + "\247r"));
                        event.setCancelled(true);
                    } else {
                        String[] list = new String[]{"join", "left", "leave", "leaving", "lobby", "server", "fell", "died", "slain", "burn", "void", "disconnect", "kill", "by", "was", "quit", "blood", "game"};
                        for (String str : list) {
                            if (packet.getChatComponent().getUnformattedText().toLowerCase().contains(str)) {
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                }

            }
        }
        super.onEvent(e);
    }

}
