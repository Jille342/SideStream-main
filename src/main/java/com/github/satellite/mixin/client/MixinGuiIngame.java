package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.listeners.EventRender2D;
import com.github.satellite.features.module.ModuleManager;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    private void renderHotbar(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        final EventRender2D eventRender2D = new EventRender2D(partialTicks);
        Satellite.onEvent(eventRender2D);
        if(ModuleManager.getModulebyName("HUD").enable)
        Satellite.hud.draw();
        if(ModuleManager.getModulebyName("HUD2").enable)
            Satellite.hud2.draw();

    }

}
