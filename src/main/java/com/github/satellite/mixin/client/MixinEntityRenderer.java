package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.listeners.EventCameraTransform;
import com.github.satellite.event.listeners.EventRenderWorld;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Shadow protected abstract void setupCameraTransform(float partialTicks, int pass);

    @Inject(method = {"renderWorldPass"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;debugView:Z")})
    private void renderWorldPassPre(int pass, float partialTicks, long finishTimeNano, CallbackInfo paramCallbackInfo) {
        EventRenderWorld e = new EventRenderWorld(partialTicks);
        Satellite.onEvent(e);
    }
    @Inject(method = {"setupCameraTransform"}, at = {@At(value = "HEAD")})
    private void setupCameraTransform(float p_78479_1_, int p_78479_2_, CallbackInfo ci) {
        EventCameraTransform e = new EventCameraTransform(p_78479_1_, p_78479_2_);
        Satellite.onEvent(e);
    }
}
