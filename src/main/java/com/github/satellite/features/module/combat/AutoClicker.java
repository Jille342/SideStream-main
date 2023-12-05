package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.PlayerHelper;
import com.github.satellite.utils.ServerHelper;
import com.github.satellite.utils.TimeHelper;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoClicker extends Module {
//    private final BooleanSetting ignoreFriendsSetting = registerSetting(BooleanSetting.builder()
    //          .name("Ignore Friends")
    //          .value(true)
    //        .build()
    // );





    private final TimeHelper leftStopWatch = new TimeHelper();
    private final TimeHelper rightStopWatch = new TimeHelper();

    private boolean attacked;
    private boolean clicked;
    private int breakTick;

    BooleanSetting leftClickSetting;

    BooleanSetting ignoreTeamsSetting;
    NumberSetting leftCpsSetting;
    NumberSetting rightCpsSetting;
    BooleanSetting rightClickSetting;
    public AutoClicker() {
        super("Auto Clicker", Keyboard.KEY_NONE, Category.COMBAT);



    }
    @Override
    public void init() {
        super.init();
        this.leftClickSetting = new BooleanSetting("LeftClick", true);
        this.ignoreTeamsSetting = new BooleanSetting("IgnoreTeams", true);
        this.rightCpsSetting = new NumberSetting("RightCPS", 7, 0, 20, 1f);
        this.leftCpsSetting = new NumberSetting("LeftCPS", 7, 0, 20, 1f);
        this.rightClickSetting = new BooleanSetting("RightClick", true);


        addSetting(leftClickSetting, ignoreTeamsSetting, rightClickSetting, rightCpsSetting, leftClickSetting, leftCpsSetting);
    }

    @Override
    public void onDisable() {
        attacked = false;
        clicked = false;
        breakTick = 0;
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if (mc.gameSettings.keyBindAttack.isKeyDown() && shouldClick(true)) {
                doLeftClick();
            }

            if (rightClickSetting.enable && mc.gameSettings.keyBindUseItem.isKeyDown() && shouldClick(false)) {
                doRightClick();
            }
        }
    }


    private void doLeftClick() {
        int cps = (int) leftCpsSetting.getValue();
        if (attacked && mc.player.ticksExisted % RandomUtils.nextInt(1, 3) == 0) {
            PlayerHelper.holdState(0, false);
            attacked = false;
            return;
        }

        if (!leftStopWatch.hasReached(calculateTime(cps))) {
            return;
        }

        PlayerHelper.holdState(0, true);
        PlayerHelper.legitAttack();
        attacked = true;
    }

    private void doRightClick() {
        int cps = (int) rightCpsSetting.getValue();
        if (clicked && mc.player.ticksExisted % RandomUtils.nextInt(1, 3) == 0) {
            PlayerHelper.holdState(1, false);
            clicked = false;
            return;
        }

        if (!rightStopWatch.hasReached(calculateTime(cps))) {
            return;
        }

        PlayerHelper.holdState(1, true);
        PlayerHelper.legitClick();
        clicked = true;
    }

    private long calculateTime(int cps) {
        return (long) ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);
    }

    public boolean shouldClick(boolean left) {
        if (mc.isGamePaused() || !mc.inGameHasFocus) {
            return false;
        }

        if (mc.player.getItemInUseCount() > 0) {
            return false;
        }

        if (mc.objectMouseOver != null && left) {
            RayTraceResult result = mc.objectMouseOver;
            if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockPos = result.getBlockPos();
                Block block = mc.world.getBlockState(blockPos).getBlock();
                if (block instanceof BlockAir || block instanceof BlockLiquid) {
                    return true;
                }

                if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                    if (breakTick > 1) {
                        return false;
                    }
                    breakTick++;
                } else {
                    breakTick = 0;
                }
            } else {
                breakTick = 0;

                if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
                    Entity entity = result.entityHit;
                    if (entity instanceof EntityPlayer && !entity.isDead) {
                        return (!ignoreTeamsSetting.enable || !ServerHelper.isTeammate((EntityPlayer) entity));
                    }
                }
            }
        }
        return true;
    }
}
