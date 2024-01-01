package client.features.module.combat;


import client.event.Event;
import client.event.listeners.EventTick;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;

import java.util.List;

public class Reach2  extends Module {

    static NumberSetting extendedReach;
    static BooleanSetting hitThroughWalls;

    public Reach2() {
        super("Reach2", 0, Category.COMBAT);


    }
    @Override
    public void init(){
        super.init();
        hitThroughWalls = new BooleanSetting("Hit Through Walls", false);
        extendedReach = new NumberSetting("Extended Reach", 3.0,0, 4.0 ,0.1);
        addSetting(extendedReach, hitThroughWalls);

    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            if(mc.world != null  && mc.player != null) {
                return;
            }
            if( Mouse.isButtonDown(0))
            call();
        }
    }
    public static void call() {
        if (!hitThroughWalls.enable && mc.objectMouseOver != null) {
            BlockPos p = mc.objectMouseOver.getBlockPos();
            if(p == null)
                return;
            if ( mc.world.getBlockState(p).getBlock() != Blocks.AIR) {
                return;
            }
        }

        double r = extendedReach.getValue();
        Object[] o = zz(r, 0.0D);
        if (o == null) {
        } else {
            Entity en = (Entity)o[0];
            mc.objectMouseOver = new RayTraceResult(en, (Vec3d) o[1]);
            mc.pointedEntity = en;
        }
    }
    private static Object[] zz(double zzD, double zzE) {
        if (!ModuleManager.getModulebyClass(Reach2.class).enable) {
            zzD = mc.playerController.extendedReach() ? 6.0D : 3.0D;
        }

        Entity entity1 = mc.getRenderViewEntity();
        Entity entity = null;
        if (entity1 == null) {
            return null;
        } else {
            mc.profiler.startSection("pick");
            Vec3d eyes_positions = entity1.getPositionEyes(1.0F);
            Vec3d look = entity1.getLook(1.0F);
            Vec3d new_eyes_pos = eyes_positions.add(look.x * zzD, look.y * zzD, look.z * zzD);
            Vec3d zz6 = null;
            List<Entity> zz8 = mc.world.getEntitiesWithinAABBExcludingEntity(entity1, entity1.getEntityBoundingBox().expand(look.x * zzD, look.y * zzD, look.z * zzD).expand(1.0D, 1.0D, 1.0D));
            double zz9 = zzD;

            for (Entity o : zz8) {
                if (o.canBeCollidedWith()) {
                    float ex = (float) ((double) o.getCollisionBorderSize() * HitBox.exp(o));
                    AxisAlignedBB zz13 = o.getEntityBoundingBox().expand(ex, ex, ex);
                    zz13 = zz13.expand(zzE, zzE, zzE);
                    RayTraceResult zz14 = zz13.calculateIntercept(eyes_positions, new_eyes_pos);
                    if (zz13.contains(eyes_positions)) {
                        if (0.0D < zz9 || zz9 == 0.0D) {
                            entity = o;
                            zz6 = zz14 == null ? eyes_positions : zz14.hitVec;
                            zz9 = 0.0D;
                        }
                    } else if (zz14 != null) {
                        double zz15 = eyes_positions.distanceTo(zz14.hitVec);
                        if (zz15 < zz9 || zz9 == 0.0D) {
                            if (o == entity1.getRidingEntity()) {
                                if (zz9 == 0.0D) {
                                    entity = o;
                                    zz6 = zz14.hitVec;
                                }
                            } else {
                                entity = o;
                                zz6 = zz14.hitVec;
                                zz9 = zz15;
                            }
                        }
                    }
                }
            }

            if (zz9 < zzD && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
                entity = null;
            }

            mc.profiler.endSection();
            if (entity != null && zz6 != null) {
                return new Object[]{entity, zz6};
            } else {
                return null;
            }
        }
    }


}
