package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.lang.reflect.Field;

public class AntiForgeBypass extends Module {
    public AntiForgeBypass() {
        super("AntiForgeBypass",0, Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventPacket) {
            if (!mc.isSingleplayer()) {
                EventPacket event = ((EventPacket) e);
                if (event.isOutgoing()) {
                    Packet<?> p = event.getPacket();
                    if (p instanceof FMLProxyPacket) {
                        event.setCancelled(true);
                    } else if (p instanceof CPacketCustomPayload) {
                        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer()).writeString("vanilla");
                        CPacketCustomPayload payloadPacket = (CPacketCustomPayload) event.getPacket();
                        if (!payloadPacket.getChannelName().equals("MC|Brand")) return;
                        try {
                            final Field field = payloadPacket.getClass().getDeclaredField("data");
                            field.setAccessible(true);
                            field.set(payloadPacket, packetBuffer);
                        }catch(NoSuchFieldException | IllegalAccessException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

