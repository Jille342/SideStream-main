package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.PlayerHelper;
import com.github.satellite.utils.ServerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AimAssist extends Module {








    private final List<EntityLivingBase> validated = new ArrayList<>();
    private EntityLivingBase primary;
    private int breakTick;
    BooleanSetting ignoreTeamsSetting;
    BooleanSetting notHolding;
    NumberSetting aimSpeedSetting;
    NumberSetting rangeSetting;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    NumberSetting fov;

    public AimAssist() {
        super("Aim Assist",  Keyboard.KEY_NONE,Category.COMBAT);
    }
    @Override
    public void init() {
        super.init();
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.notHolding = new BooleanSetting("not Holding", false);
        this.aimSpeedSetting = new NumberSetting("AimSpeed", 0.45, 0.1, 1.0, 0.1);
        this.rangeSetting = new NumberSetting("Range", 5.0, 3.0, 8.0, 0.1);
        this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);


        addSetting(notHolding, ignoreTeamsSetting, aimSpeedSetting, rangeSetting,  targetAnimalsSetting, targetMonstersSetting, fov);
    }

    @Override
    public void onDisable() {
        validated.clear();
        primary = null;
        breakTick = 0;
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            primary = findTarget();
            if (e.isPost() || primary == null || !canAssist()) {
                return;
            }

            float diff = calculateYawChangeToDst(primary);
            float aimSpeed = (float) aimSpeedSetting.value;
            aimSpeed = (float) MathHelper.clamp(RandomUtils.nextFloat(aimSpeed - 0.2f, aimSpeed + 1.8f), aimSpeedSetting.minimum, aimSpeedSetting.maximum);
            aimSpeed -= aimSpeed % getSensitivity();

            if (diff < -6) {
                aimSpeed -= diff / 12f;
                mc.player.rotationYaw -= aimSpeed;
            } else if (diff > 6) {
                aimSpeed += diff / 12f;
                mc.player.rotationYaw += aimSpeed;
            }
        }

    }

    public double getSensitivity() {
        double sensitivity = mc.gameSettings.mouseSensitivity * 0.3 + 0.2;
        return sensitivity * sensitivity * sensitivity * RandomUtils.nextFloat(2f, 3f);
    }

    private boolean canAssist() {
        if (mc.isGamePaused() || !mc.inGameHasFocus || mc.currentScreen != null) {
            return false;
        }

        if (!notHolding.enable && !mc.gameSettings.keyBindAttack.isKeyDown()) {
            return false;
        }

        if (mc.player.getItemInUseCount() > 0) {
            return false;
        }

        if (mc.objectMouseOver != null) {
            RayTraceResult result = mc.objectMouseOver;
            if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockPos = result.getBlockPos();
                Block block = mc.world.getBlockState(blockPos).getBlock();
                if (block instanceof BlockAir || block instanceof BlockLiquid) {
                    return true;
                }

                if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                    if (breakTick > 2) {
                        return false;
                    }
                    breakTick++;
                } else {
                    breakTick = 0;
                }
            }
        }
        return true;
    }

    private EntityLivingBase findTarget() {
        validated.clear();

        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity != mc.player) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }

                if (!PlayerHelper.fov(entity, fov.value))
                    continue;
                double focusRange = mc.player.canEntityBeSeen(entity) ? rangeSetting.value : 3.5;
                if (mc.player.getDistance(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }

                    validated.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityAnimal && targetAnimalsSetting.enable) {
                    validated.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityMob && targetMonstersSetting.enable) {
                    validated.add((EntityLivingBase) entity);
                }
            }
        }

        if (validated.isEmpty()) return null;
        validated.sort(Comparator.comparingDouble(this::calculateYawChangeToDst));
        return validated.get(0);
    }

    public float calculateYawChangeToDst(Entity entity) {
        double diffX = entity.posX - mc.player.posX;
        double diffZ = entity.posZ - mc.player.posZ;
        double deg = Math.toDegrees(Math.atan(diffZ / diffX));
        if (diffZ < 0.0 && diffX < 0.0) {
            return (float) MathHelper.wrapDegrees(-(mc.player.rotationYaw - (90 + deg)));
        } else if (diffZ < 0.0 && diffX > 0.0) {
            return (float) MathHelper.wrapDegrees(-(mc.player.rotationYaw - (-90 + deg)));
        } else {
            return (float) MathHelper.wrapDegrees(-(mc.player.rotationYaw - Math.toDegrees(-Math.atan(diffX / diffZ))));
        }
    }
}
