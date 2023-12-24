package client.mixin.client;

import client.features.module.Module;
import client.features.module.ModuleManager;
import client.Client;
import client.event.listeners.EventCameraTransform;
import client.event.listeners.EventRenderWorld;
import client.features.module.combat.Hitbox;
import client.features.module.combat.Reach;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    Minecraft mc = Minecraft.getMinecraft();
    @Shadow
    private Entity pointedEntity;
    @Shadow protected abstract void setupCameraTransform(float partialTicks, int pass);

    @Inject(method = {"renderWorldPass"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;debugView:Z")})
    private void renderWorldPassPre(int pass, float partialTicks, long finishTimeNano, CallbackInfo paramCallbackInfo) {
        EventRenderWorld e = new EventRenderWorld(partialTicks);
        Client.onEvent(e);
    }
    @Inject(method = {"setupCameraTransform"}, at = {@At(value = "HEAD")})
    private void setupCameraTransform(float p_78479_1_, int p_78479_2_, CallbackInfo ci) {
        EventCameraTransform e = new EventCameraTransform(p_78479_1_, p_78479_2_);
        Client.onEvent(e);
    }
    @Inject(method = "getMouseOver", at = @At("HEAD"), cancellable = true)
    public void getMouseOver(float partialTicks, CallbackInfo ci) {
        Module reachCheat = ModuleManager.getModulebyClass(Reach.class);
        if (reachCheat == null || !reachCheat.isEnable()) {
            return;
        }

        double reach = Reach.reach.getValue();
        Module hitBoxCheat = ModuleManager.getModulebyClass(Hitbox.class);
        if (hitBoxCheat != null && hitBoxCheat.isEnable()) {
            reach -= Hitbox.size.getValue();
            if (reach < 3.1) {
                reach = 3.1;
            }
        }

        ci.cancel();

        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity != null) {
            if (mc.world != null) {
                mc.profiler.startSection("pick");
                mc.pointedEntity = null;

                double reachDistance = mc.playerController.getBlockReachDistance();
                mc.objectMouseOver = viewEntity.rayTrace(reachDistance, partialTicks);

                Vec3d eyes = viewEntity.getPositionEyes(partialTicks);
                double realReachDistance = reachDistance;
                boolean isFar = false;

                if (mc.playerController.extendedReach()) {
                    realReachDistance = 6;
                    reachDistance = realReachDistance;
                } else if (reachDistance > reach) {
                    isFar = true;
                }

                if (mc.objectMouseOver != null) {
                    realReachDistance = mc.objectMouseOver.hitVec.distanceTo(eyes);
                }

                pointedEntity = null;

                double reachCopy = realReachDistance;
                Vec3d look = viewEntity.getLook(1);
                Vec3d hit = null;
                List<Entity> excludingEntity = mc.world.getEntitiesInAABBexcluding(
                        viewEntity,
                        viewEntity.getEntityBoundingBox().expand(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance).grow(1, 1, 1),
                        Predicates.and(EntitySelectors.NOT_SPECTATING, entity -> entity != null && entity.canBeCollidedWith())
                );

                for (Entity entity : excludingEntity) {
                    AxisAlignedBB hittableBox = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
                    RayTraceResult raytraceresult = hittableBox.calculateIntercept(eyes, eyes.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance));

                    if (hittableBox.contains(eyes) && reachCopy >= 0) {
                        pointedEntity = entity;
                        hit = raytraceresult == null ? eyes : raytraceresult.hitVec;
                        reachCopy = 0;
                    } else if (raytraceresult != null) {
                        double distance = eyes.distanceTo(raytraceresult.hitVec);
                        if (distance < reachCopy || reachCopy == 0) {
                            if (entity.getLowestRidingEntity() == viewEntity.getLowestRidingEntity() && !entity.canRiderInteract()) {
                                if (reachCopy == 0) {
                                    pointedEntity = entity;
                                    hit = raytraceresult.hitVec;
                                }
                            } else {
                                pointedEntity = entity;
                                hit = raytraceresult.hitVec;
                                reachCopy = distance;
                            }
                        }
                    }
                }

                if (pointedEntity != null && hit != null && isFar && eyes.distanceTo(hit) > reach) {
                    pointedEntity = null;
                    mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, hit, null, new BlockPos(hit));
                }

                if (pointedEntity != null && hit != null && (reachCopy < realReachDistance || mc.objectMouseOver == null)) {
                    mc.objectMouseOver = new RayTraceResult(pointedEntity, hit);
                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                        mc.pointedEntity = pointedEntity;
                    }
                }

                mc.profiler.endSection();
            }
        }
    }

}
