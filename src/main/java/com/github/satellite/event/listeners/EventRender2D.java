package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;
import net.minecraft.client.gui.ScaledResolution;

/**
 * 2DRenderのEvent
 */
public class EventRender2D extends Event {
    private ScaledResolution resolution;
    private float partialticks;

    public EventRender2D( float partialticks) {
        this.resolution = resolution;
        this.partialticks = partialticks;
    }

    public ScaledResolution getResolution() {
        return resolution;
    }

    public float getPartialTicks() {
        return partialticks;
    }
}