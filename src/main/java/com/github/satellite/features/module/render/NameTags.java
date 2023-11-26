package com.github.satellite.features.module.render;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.RenderingUtils;
import com.github.satellite.utils.font.CFontRenderer;
import com.github.satellite.utils.font.Fonts;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Timer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author CyberTF2
 * Rewrote by Hoosiers
 */


public class NameTags extends Module {

    private final CFontRenderer font = Fonts.default18;
    Timer timer = new Timer(2.0F);
    BooleanSetting showItems;
    BooleanSetting showDurability;
    BooleanSetting renderSelf;

    BooleanSetting showHealth;
    public NameTags() {
        super("NameTags", Keyboard.KEY_NONE, Module.Category.RENDER);

    }

    @Override
    public void init() {
        super.init();
           showItems = new BooleanSetting("Show Items", true);
        showDurability = new BooleanSetting("Show Durability", true);
        showHealth = new BooleanSetting("Show Health", true);
        renderSelf = new BooleanSetting("Render Self", false);
        addSetting(showDurability, showHealth,showItems,renderSelf);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventRenderWorld) {
            if (mc.player == null || mc.world == null) {
                return;
            }

            mc.world.playerEntities.stream().filter(this::shouldRender).forEach(entityPlayer -> {
                Vec3d vec3d = findEntityVec3d(entityPlayer);
                renderNameTags(entityPlayer, vec3d.x, vec3d.y, vec3d.z);
            });
        }
    }

    private boolean shouldRender(EntityPlayer entityPlayer) {
        if (entityPlayer == mc.player && !renderSelf.enable) return false;

        if (entityPlayer.isDead || entityPlayer.getHealth() <= 0) return false;

        return true;
    }

    private Vec3d findEntityVec3d(EntityPlayer entityPlayer) {
        double posX = balancePosition(entityPlayer.posX, entityPlayer.lastTickPosX);
        double posY = balancePosition(entityPlayer.posY, entityPlayer.lastTickPosY);
        double posZ = balancePosition(entityPlayer.posZ, entityPlayer.lastTickPosZ);

        return new Vec3d(posX, posY, posZ);
    }

    private double balancePosition(double newPosition, double oldPosition) {
        return oldPosition + (newPosition - oldPosition) * this.timer.renderPartialTicks;
    }

    private void renderNameTags(EntityPlayer entityPlayer, double posX, double posY, double posZ) {
        double adjustedY = posY + (entityPlayer.isSneaking() ? 1.9 : 2.1);

        String[] name = new String[1];
        name[0] = buildEntityNameString(entityPlayer);

        RenderingUtils.drawNametag(posX, adjustedY, posZ, name, findTextColor(entityPlayer), 2);
        renderItemsAndArmor(entityPlayer, 0, 0);
        GlStateManager.popMatrix();
    }

    private String buildEntityNameString(EntityPlayer entityPlayer) {
        String name = entityPlayer.getName();







        if (showHealth.enable) {
            int health = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
            TextFormatting textFormatting = findHealthColor(health);

            name = name + " " + textFormatting + health;
        }

        return name;
    }

    private TextFormatting findHealthColor(int health) {
        if (health <= 0) {
            return TextFormatting.DARK_RED;
        } else if (health <= 5) {
            return TextFormatting.RED;
        } else if (health <= 10) {
            return TextFormatting.GOLD;
        } else if (health <= 15) {
            return TextFormatting.YELLOW;
        } else if (health <= 20) {
            return TextFormatting.DARK_GREEN;
        }

        return TextFormatting.GREEN;
    }

    private int findTextColor(EntityPlayer entityPlayer) {

       if (entityPlayer.isInvisible()) {
            return new Color(128, 128, 128).getRGB();
        } else if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) == null) {
            return new Color(239, 1, 71).getRGB();
        } else if (entityPlayer.isSneaking()) {
            return new Color(255, 153, 0).getRGB();
        }

        return new Color(255, 255, 255).getRGB();
    }

    private void renderItemsAndArmor(EntityPlayer entityPlayer, int posX, int posY) {
        ItemStack mainHandItem = entityPlayer.getHeldItemMainhand();
        ItemStack offHandItem = entityPlayer.getHeldItemOffhand();

        int armorCount = 3;
        for (int i = 0; i <= 3; i++) {
            ItemStack itemStack = entityPlayer.inventory.armorInventory.get(armorCount);

            if (!itemStack.isEmpty()) {
                posX -= 8;


            }
            armorCount --;
        }

        if (!mainHandItem.isEmpty() && (showItems.enable || showDurability.enable && offHandItem.isItemStackDamageable())) {
            posX -= 8;

            int enchantSize = EnchantmentHelper.getEnchantments(offHandItem).size();
            if (showItems.enable && enchantSize > posY) {
                posY = enchantSize;
            }
        }

        if (!mainHandItem.isEmpty()) {



            int armorY = findArmorY(posY);



            if (showItems.enable) {
                renderItem(mainHandItem, posX, armorY, posY);
                armorY -= 32;
            }

            if (showDurability.enable && mainHandItem.isItemStackDamageable()) {
                renderItemDurability(mainHandItem, posX, armorY);
            }


            armorY -= (mc.fontRenderer.FONT_HEIGHT);



            if (showItems.enable && mainHandItem.isItemStackDamageable()) {
                posX += 16;
            }
        }

        int armorCount2 = 3;
        for (int i = 0; i <= 3; i++) {
            ItemStack itemStack = entityPlayer.inventory.armorInventory.get(armorCount2);

            if (!itemStack.isEmpty()) {
                int armorY = findArmorY(posY);

                if (showItems.enable) {
                    renderItem(itemStack, posX, armorY, posY);
                    armorY -= 32;
                }

                if (showDurability.enable && itemStack.isItemStackDamageable()) {
                    renderItemDurability(itemStack, posX, armorY);
                }
                posX += 16;
            }
            armorCount2--;
        }

        if (!offHandItem.isEmpty()) {
            int armorY = findArmorY(posY);

            if (showItems.enable) {
                renderItem(offHandItem, posX, armorY, posY);
                armorY -= 32;
            }

            if (showDurability.enable && offHandItem.isItemStackDamageable()) {
                renderItemDurability(offHandItem, posX, armorY);
            }
        }
    }

    private int findArmorY(int posY) {
        int posY2 = showItems.enable ? -26 : -27;
        if (posY > 4) {
            posY2 -= (posY - 4) * 8;
        }

        return posY2;
    }


    private void renderItemDurability(ItemStack itemStack, int posX, int posY) {
        float damagePercent = (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();

        float green = damagePercent;
        if (green > 1) green = 1;
        else if (green < 0) green = 0;

        float red = 1 - green;

        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        font.drawStringWithShadow( (int) (damagePercent * 100) + "%", posX * 2, posY, new Color((int) (red * 255), (int) (green * 255), 255).getRGB());
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }

    private void renderItem(ItemStack itemStack, int posX, int posY, int posY2) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();

        final int posY3 = (posY2 > 4) ? ((posY2 - 4) * 8 / 2) : 0;

        mc.getRenderItem().zLevel = -150.0f;
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, posX, posY + posY3);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, posX, posY + posY3);
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0f;
        RenderingUtils.prepare();
        GlStateManager.pushMatrix();
        GlStateManager.scale(.5, .5, .5);
        renderEnchants(itemStack, posX, posY - 24);
        GlStateManager.popMatrix();
    }

    private void renderEnchants(ItemStack itemStack, int posX, int posY) {
        GlStateManager.enableTexture2D();

        for (Enchantment enchantment : EnchantmentHelper.getEnchantments(itemStack).keySet()) {
            if (enchantment == null) {
                continue;
            }

            posY += 8;
        }


        GlStateManager.disableTexture2D();
    }


}
