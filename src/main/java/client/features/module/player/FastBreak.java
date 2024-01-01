package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.TimeHelper;
import net.minecraft.block.*;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Field;

public class FastBreak extends Module {

    public FastBreak() {
        super("FastBreak", 0, Category.PLAYER);
    }
    ModeSetting mode;
    NumberSetting  speed;
    NumberSetting delay;
    BooleanSetting nexusProtection;
    TimeHelper time = new TimeHelper();

    private boolean bzs = false;
    private float bzx = 0.0f;
    public BlockPos blockPos;
    public EnumFacing facing;


    public void init() {
        mode = new ModeSetting("Mode ", "Packet", new String[]{"Potion", "Packet"});
        this.delay = new NumberSetting("Vanilla Delay", 100, 100, 1000, 100F);

        this.speed = new NumberSetting("Potion Speed", 1, 0, 4, 1f);
        this.nexusProtection = new BooleanSetting("Nexus Protection", true);
        addSetting(speed, mode, delay,nexusProtection); super.init();

    }

    public void onEvent(Event<?> event) {
        if(event instanceof EventUpdate) {
            setTag(mode.getMode());
            if(nexusProtection.enable) {
                if (mc.playerController.getIsHittingBlock() ){
                    if (mc.objectMouseOver != null) {
                        BlockPos p = mc.objectMouseOver.getBlockPos();
                        if (p != null) {
                            Block bl = mc.world.getBlockState(p).getBlock();
                            if (bl == Blocks.END_STONE) {
                                removePotionEffect();
                                return;
                            }

                        }
                    }
                }
            }
            if (mode.getMode().equals("Potion")) {
            } else {
                this.removePotionEffect();
            }
            if(mode.getMode().equals("Packet")) {
                if (this.bzs) {
                    Block block = mc.world.getBlockState(this.blockPos).getBlock();
                    this.bzx += (float) ((double) block.getPlayerRelativeBlockHardness(mc.world.getBlockState(blockPos), mc.player, mc.world, blockPos) * 1.4);
                    if (this.bzx >= 1.0f) {
                        mc.world.setBlockState(this.blockPos, Blocks.AIR.getDefaultState(), 11);
                        mc.player.connection.getNetworkManager().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing));
                        this.bzx = 0.0f;
                        this.bzs = false;
                    }
                }
            }
        }
        if(event instanceof EventPacket) {
            EventPacket eventPacket = ((EventPacket) event);
            if (eventPacket.getPacket() instanceof CPacketPlayerDigging) {
                CPacketPlayerDigging  c07PacketPlayerDigging = (CPacketPlayerDigging) eventPacket.getPacket();
                if (c07PacketPlayerDigging.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                    this.bzs = true;
                    this.blockPos = c07PacketPlayerDigging.getPosition();
                    this.facing = c07PacketPlayerDigging.getFacing();
                    this.bzx = 0.0f;
                } else if (c07PacketPlayerDigging.getAction() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                    this.bzs = false;
                    this.blockPos = null;
                    this.facing = null;
                }
            }
        }

    }

    public void removePotionEffect(){
    }

    public void onDisable() {
        super.onDisable();
        this.removePotionEffect();
    }
}