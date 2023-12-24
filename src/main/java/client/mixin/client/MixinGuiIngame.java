package client.mixin.client;

import client.features.module.ModuleManager;
import client.Client;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventRenderGUI;
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
        Client.onEvent(eventRender2D);
        if(ModuleManager.getModulebyName("HUD").enable)
        Client.hud.draw();
        if(ModuleManager.getModulebyName("HUD2").enable)
            Client.hud2.draw();

    }

    @Inject( method= "renderGameOverlay", at = @At("HEAD") )
    private void renderGameOverlay(float partialTicks, CallbackInfo ci) {
     final EventRenderGUI eventRenderGUI = new EventRenderGUI();
     Client.onEvent(eventRenderGUI);
    }
}
