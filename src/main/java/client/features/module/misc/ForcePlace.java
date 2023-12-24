package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

public class ForcePlace extends Module {

    public ForcePlace() {
        super("ForcePlace", 0, Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventPacket) {
        	if (((EventPacket) e).getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
        		if (!mc.player.isSneaking()) {
            		if (e.isPre()) {
            			mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            		}
            		if (e.isPost()) {
            			mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
            		}
        		}
        	}
        }
        super.onEvent(e);
    }
}
