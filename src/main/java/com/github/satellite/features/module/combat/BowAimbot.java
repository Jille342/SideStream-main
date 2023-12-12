/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.PlayerHelper;
import com.github.satellite.utils.RotationUtils;
import com.github.satellite.utils.ServerHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BowAimbot extends Module
{
    private static final AxisAlignedBB TARGET_BOX =
            new AxisAlignedBB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);

    private Entity target;
    private float velocity;

    private final List<EntityLivingBase> validated = new ArrayList<>();


    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;
    NumberSetting fov;
    public BowAimbot()
    {
        super("BowAimbot", Keyboard.KEY_NONE, Module.Category.COMBAT);
    }



    public void init() {
        super.init();
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.fov = new NumberSetting("FOV", 40.0D, 15.0D, 360.0D, 1.0D);


        addSetting( ignoreTeamsSetting,targetAnimalsSetting, targetMonstersSetting, fov);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            // check if using item
            if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                target = null;
                return;
            }


            // check if item is bow
            ItemStack item = mc.player.inventory.getCurrentItem();
            if (item == null || !(item.getItem() instanceof ItemBow)) {
                return;
            }

            // set target

            target = this.findTarget();
            if (target == null)
                return;

            // set velocity
            velocity = (72000 - mc.player.getItemInUseCount()) / 20F;
            velocity = (velocity * velocity + velocity * 2) / 3;
            if (velocity > 1)
                velocity = 1;

            // adjust for FastBow


            // set position to aim at
            double d = RotationUtils.getEyesPos()
                    .distanceTo(target.getEntityBoundingBox().getCenter());
            double posX = target.posX + (target.posX - target.prevPosX) * d
                    - mc.player.posX;
            double posY = target.posY + (target.posY - target.prevPosY) * d
                    + target.height * 0.5 - mc.player.posY
                    - mc.player.getEyeHeight();
            double posZ = target.posZ + (target.posZ - target.prevPosZ) * d
                    - mc.player.posZ;

            // set yaw
            mc.player.rotationYaw =
                    (float) Math.toDegrees(Math.atan2(posZ, posX)) - 90;

            // calculate needed pitch
            double hDistance = Math.sqrt(posX * posX + posZ * posZ);
            double hDistanceSq = hDistance * hDistance;
            float g = 0.006F;
            float velocitySq = velocity * velocity;
            float velocityPow4 = velocitySq * velocitySq;
            float neededPitch = (float) -Math.toDegrees(Math.atan((velocitySq - Math
                    .sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq)))
                    / (g * hDistance)));

            // set pitch
            if (Float.isNaN(neededPitch))
                RotationUtils.faceEntityClient(target);
            else
                mc.player.rotationPitch = neededPitch;
        }
        super.onEvent(e);
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
    private EntityLivingBase findTarget() {
        validated.clear();

        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity != mc.player) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }
                if (!mc.player.canEntityBeSeen(entity))
                    continue;

                if (!PlayerHelper.fov(entity, fov.value))
                    continue;
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

}