package client.mixin.client;

import client.Client;
import client.event.EventDirection;
import client.event.EventType;
import client.event.listeners.EventPacket;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Shadow private SocketAddress socketAddress;

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void preSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacket e = new EventPacket(packet);
        e.setDirection(EventDirection.OUTGOING);
        e.setType(EventType.PRE);
        Client.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("RETURN"), cancellable = true)
    private void postSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacket e = new EventPacket(packet);
        e.setDirection(EventDirection.OUTGOING);
        e.setType(EventType.POST);
        Client.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void ChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacket e = new EventPacket(packet);
        e.setDirection(EventDirection.INCOMING);
        Client.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
