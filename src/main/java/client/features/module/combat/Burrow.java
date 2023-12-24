package client.features.module.combat;

import org.lwjgl.input.Keyboard;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.BlockUtils;
import client.utils.InventoryUtils;
import client.utils.MovementUtils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Burrow extends Module {
	
	public Burrow() {
		super("Burrow", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	NumberSetting range;
	BooleanSetting checkInHole;

	@Override
	public void init() {
		super.init();
		this.checkInHole = new BooleanSetting("CheckInHole", null, true);
		this.range = new NumberSetting("Range", null, 5.2, 0, 100, .1);
		addSetting(range, checkInHole);
	}
	
	BlockPos render;
	int progress;
	int sleep;
	
	@Override
	public void onEnable() {
		int pistem = InventoryUtils.pickItem(33, false);
		int powtem = InventoryUtils.pickItem(152, false);
		int obitem = InventoryUtils.pickItem(49, false);
		BlockPos pos = new BlockPos(mc.player);
		
		InventoryUtils.setSlot(obitem);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.add(0, -1, 1), 0, false), false);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.add(0, 0, 1), 0, false), false);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.add(0, 1, 1), 0, false), false);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.add(0, 2, 1), 0, false), false);
		
		InventoryUtils.setSlot(pistem);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.offset(EnumFacing.UP, 2), 0, false), false);
		
		InventoryUtils.setSlot(powtem);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.offset(EnumFacing.UP, 3), 0, false), false);
		
		progress = 0;
	}
	
	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {
			int obsiitem = InventoryUtils.pickItem(49, false);
			BlockPos pos = new BlockPos(mc.player);
			
			if (progress==1) {
				double y = mc.player.posY;
				MovementUtils.vClip(-2);
			}
			if (progress==2) {
				InventoryUtils.setSlot(obsiitem);
				BlockUtils.doPlace(BlockUtils.isPlaceable(pos.offset(EnumFacing.UP, 2), 0, false), false);
				MovementUtils.vClip(2);
			}
			if (progress == 3) {
				toggle();
			}
			
			progress ++;
		}
	}
	
	@Override
	public void onDisable() {
		
	}
}