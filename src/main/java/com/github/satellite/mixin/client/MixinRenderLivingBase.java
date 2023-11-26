package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventNameTag;
import com.github.satellite.features.module.ModuleManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase>  {





    @Inject(method = {"renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V"}, at = @At("HEAD"))
    public void renderName(T entity, double x, double y, double z, CallbackInfo ci) {
        EventNameTag eventNameTag = new EventNameTag();
        Satellite.onEvent(eventNameTag);
        if (eventNameTag.isCancelled()) {
            return;
        }
    }

}
