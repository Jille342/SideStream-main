package client.features.module.combat;


import client.event.Event;
import client.event.listeners.EventTick;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HitBox extends Module {
    public HitBox() {
        super("HitBox", 0,Category.COMBAT);
    }
    public  static NumberSetting size;
    private static RayTraceResult mv;
    private final List<EntityLivingBase> validated = new ArrayList<>();
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;
    NumberSetting fov;
    NumberSetting rangeSetting;
    BooleanSetting hurttime;

    public static boolean isAttackable = false;

    public  ModeSetting sortmode;
    @Override
    public void init(){
        super.init();
        sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Angle","Distance"});
        size = new NumberSetting("HitBox", 0.08 , 0, 1,0.01F);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.rangeSetting = new NumberSetting("Range", 5.0, 1, 8.0, 0.1);
        this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);
        hurttime = new BooleanSetting("HurtTime", true);
        addSetting(hurttime,rangeSetting,size, sortmode, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, fov);

    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            setTag(sortmode.getMode());
            gmo(1);
        }
        if(e instanceof EventTick) {
            if(mc.world != null &&   mc.player != null) {
                if (ModuleManager.getModulebyClass(AutoClicker.class).enable && mc.gameSettings.keyBindAttack.isKeyDown()) {
                    if (mv != null) {
                        mc.objectMouseOver = mv;
                    }
                }
            }
        }
    }
    public static double exp(Entity entity) {
        return ( ModuleManager.getModulebyClass(HitBox.class).isEnable() ) ? size.getValue() : 1.0D;
    }
    public void gmo(float partialTicks) {
        if (mc.getRenderViewEntity() != null && mc.world != null) {
            mc.pointedEntity = null;
            Entity pE = null;
            double d0 = 3.0D;
            mv = mc.getRenderViewEntity().rayTrace(d0, partialTicks);
            double d2 = d0;
            Vec3d vec3 = mc.getRenderViewEntity().getPositionEyes(partialTicks);
            if (mv != null) {
                d2 = mv.hitVec.distanceTo(vec3);
            }

            Vec3d vec4 = mc.getRenderViewEntity().getLook(partialTicks);
            Vec3d vec5 = vec3.add(vec4.x * d0, vec4.y * d0, vec4.z * d0);
            Vec3d vec6 = null;
            float f1 = 1.0F;
            double d3 = d2;
            Entity entity = findTarget();
            if(entity != null) {
                if (entity.canBeCollidedWith()) {
                    float ex = (float) ((double) entity.getCollisionBorderSize() * exp(entity));
                    AxisAlignedBB ax = entity.getEntityBoundingBox().expand(ex, ex, ex);
                    RayTraceResult mop = ax.calculateIntercept(vec3, vec5);
                    if (ax.contains(vec3)) {
                        if (0.0D < d3 || d3 == 0.0D) {
                            pE = entity;
                            vec6 = mop == null ? vec3 : mop.hitVec;
                            d3 = 0.0D;
                        }
                    } else if (mop != null) {
                        double d4 = vec3.distanceTo(mop.hitVec);
                        if (d4 < d3 || d3 == 0.0D) {
                            if (entity == mc.getRenderViewEntity().getRidingEntity() && !entity.canRiderInteract()) {
                                if (d3 == 0.0D) {
                                    pE = entity;
                                    vec6 = mop.hitVec;
                                }
                            } else {
                                pE = entity;
                                vec6 = mop.hitVec;
                                d3 = d4;
                            }
                        }
                    }
                }
            }


            if (pE != null && (d3 < d2 || mv == null)) {
                mv = new RayTraceResult(pE, vec6);
                mc.pointedEntity = pE;
                mc.objectMouseOver.entityHit = pE;
            }
        }

    }

    private EntityLivingBase findTarget() {
        validated.clear();
        float f1 = 1.0F;
        double d0 = 3.0D;
        Vec3d vec4 = mc.getRenderViewEntity().getLook(1);
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().expand(vec4.x * d0, vec4.y * d0, vec4.z * d0).expand(f1, f1, f1))) {
            if (entity instanceof EntityLivingBase && entity != mc.player) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }
                if(hurttime.enable && (((EntityLivingBase) entity).hurtTime >= 7))
                    continue;

                if (!PlayerHelper.fov(entity, fov.value))
                    continue;
                double focusRange = mc.player.canEntityBeSeen(entity) ? rangeSetting.value : 3.5;
                if (mc.player.getDistance(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }
                    if(  ((EntityPlayer) entity).getHealth() ==0)
                        continue;
                    validated.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityAnimal && targetAnimalsSetting.enable) {
                    if( ((EntityAnimal) entity).getHealth() ==0)
                        continue;
                    validated.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityMob && targetMonstersSetting.enable) {
                    if(((EntityMob) entity).getHealth() == 0)
                        continue;
                    validated.add((EntityLivingBase) entity);
                }
            }
        }

        if (validated.isEmpty()) return null;
        switch (sortmode.getMode()) {
            case "Angle":
                validated.sort(Comparator.comparingDouble(RotationUtils::calculateYawChangeToDst));
                break;
            case "Distance":
                validated.sort((o1, o2) -> (int) (o1.getDistance(mc.player) - o2.getDistance(mc.player)));
                break;
        }
        this.validated.sort(Comparator.comparingInt(o -> o.hurtTime));

        return validated.get(0);
    }
}
