package client.mixin.client;

import client.Client;
import client.event.listeners.EventNameTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase>  {

    private static float NAME_TAG_RANGE = 64.0F;
    private static float NAME_TAG_RANGE_SNEAK = 32.0F;





    @Inject(method = {"renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V"}, at = @At("HEAD"), cancellable = true)
    public void renderName(T entity, double x, double y, double z, CallbackInfo ci) {
        double d0 = entity.getDistanceSq(Minecraft.getMinecraft().getRenderManager().renderViewEntity);
        float f = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;

        if (d0 < (double) (f * f)) {
            EventNameTag eventNameTag = new EventNameTag();
            Client.onEvent(eventNameTag);
            if (eventNameTag.isCancelled()) {
               ci.cancel();
            }
        }
    }
}
