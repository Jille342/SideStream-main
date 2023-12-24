package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketUseEntity;

public class AutoSword extends Module {

    public AutoSword() {
        super("AutoSword", 0, Category.COMBAT);
    }

    public void onEvent(Event<?> e) {

        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                if (event.getPacket() instanceof CPacketUseEntity) {
                    CPacketUseEntity packetUseEntity = (CPacketUseEntity) event.getPacket();
                    if (packetUseEntity.getAction() == CPacketUseEntity.Action.ATTACK ) {

                      getBestWeapon();
                    }
                }
            }
        }
    }

    public void getBestWeapon(){
        float damageModifier = 0;
        int newItem = -1;
        for (int slot = 0; slot < 9; slot++)
        {
            ItemStack stack = mc.player.inventoryContainer.getSlot(slot).getStack();
            if(stack == null){
                continue;
            }
            if(stack.getItem() instanceof ItemSword){

                ItemSword is = (ItemSword)stack.getItem();
                float damage = is.getAttackDamage()+ (is.hasEffect(stack) ? getEnchantDamageVsEntity(stack) : 0);
                if(damage >= damageModifier){
                    newItem = slot;
                    damageModifier = damage;
                }
            }
        }
        if (newItem > -1)
        {
            mc.player.inventory.currentItem = newItem;
        }
    }

    public int getEnchantDamageVsEntity(ItemStack i)
    {

            return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, i);
        }


}