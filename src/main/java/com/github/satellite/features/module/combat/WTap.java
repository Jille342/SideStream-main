package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketUseEntity;

public class WTap extends Module {
    private boolean tapping;
    private int tick;

    public WTap() {
        super("WTap",0, Category.COMBAT);
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
        if (tapping) {
            if (tick == 2) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
                tapping = false;
            }
            tick++;
        }
    }

        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isOutgoing()) {
                if (event.getPacket() instanceof CPacketUseEntity) {
                    CPacketUseEntity packetUseEntity = (CPacketUseEntity) event.getPacket();
                    if (packetUseEntity.getAction() == CPacketUseEntity.Action.ATTACK && mc.player.isSprinting() && !tapping) {
                       mc.player.setSprinting(false);
                           tapping = true;
                        tick = 0;
                    }
                }
            }
        }
    }


}
