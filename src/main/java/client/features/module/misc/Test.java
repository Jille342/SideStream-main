package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.features.module.Module;
import client.utils.MovementUtils;

public class Test extends Module {

    public Test() {
        super("Test", 0, Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventMotion && e.isPre()) {
            if (mc.player.onGround) {
                double[] ncp = new double[] {0.425D, 0.821D, 0.699D, 0.599D};
                for (double d : ncp) {
                    MovementUtils.vClip2(d, false);
                }
                MovementUtils.vClip(.599D);
                mc.player.motionY = .425D;
            }
        }
        super.onEvent(e);
    }
}
