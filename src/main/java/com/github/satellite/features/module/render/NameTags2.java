package com.github.satellite.features.module.render;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRender2D;
import com.github.satellite.event.listeners.EventRenderGUI;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.RenderingUtils;
import com.github.satellite.utils.font.CFontRenderer;
import com.github.satellite.utils.font.Fonts;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Calendar;

public class NameTags2 extends Module {
    private final CFontRenderer font = Fonts.default18;
    BooleanSetting renderSelf;
    BooleanSetting showItems;
    public NameTags2() {
        super("NameTags2", 0, Category.RENDER);
    }

    public void init() {
        renderSelf = new BooleanSetting("Render Self", false);
        showItems = new BooleanSetting("Show Items", true);
        addSetting(renderSelf, showItems);
        super.init();
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventRenderWorld) {


            for (EntityPlayer player : mc.world.playerEntities) {
                if (player == null) continue;
                if (player.deathTime > 0) continue;
                if (player == mc.player  && !renderSelf.enable) continue;
                double x = ((player.lastTickPosX + (player.posX - player.lastTickPosX)
                        - mc.getRenderManager().viewerPosX));
                double y = ((player.lastTickPosY + (player.posY - player.lastTickPosY)
                        - mc.getRenderManager().viewerPosY));
                double z = ((player.lastTickPosZ + (player.posZ - player.lastTickPosZ)
                        - mc.getRenderManager().viewerPosZ));
                renderTag(player, x, y, z);

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
    private void renderTag(Entity entity, double x, double y, double z) {
        double dist = mc.player.getDistance(x, y, z);
        double scale = 1;
        String name = entity.getDisplayName().getFormattedText();
        name = name.replace(entity.getDisplayName().getFormattedText(), "\247f" + entity.getDisplayName().getFormattedText());

        if(entity.isSneaking() || entity.isInvisible()) {
            name = "\2479" + name;
        }
        if (entity instanceof EntityLivingBase) {
            name = name + " \247a" + ((double)Math.round((((EntityLivingBase) entity).getHealth() * 100) / 100) / 2);
        }
        scale = 0.0018 + 0.0001 * dist;
        if (dist <= 8.0) scale = 0.000245;

        float var13 = 1.6F;

        GlStateManager.pushMatrix();
    //    GlStateManager.translate(x, y, z);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();


        GL11.glTranslatef((float) x, (float) y + entity.height + 0.5F, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.disableBlend();
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
       GlStateManager.scale(-scale,  -scale, scale);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_LIGHTING);
        Tessellator var15 = Tessellator.getInstance();
        int var16 = (int) -mc.player.getDistanceSq(entity) / (int) var13;
        if (entity.isSneaking()) {
       //     var16 += 4;
        } else if (var16 < -14) {
            var16 = -14;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        int var17 = font.getStringWidth(name) / 2;
        RenderUtils.drawBorderedRect(-var17 - 2, var16, var17 + 2, 11 + var16, 0.5F, 0xFF000000, 0x80000000);

        font.drawStringWithShadow(name, -var17, var16, 0xFFFFFF);

        mc.entityRenderer.disableLightmap();
        GL11.glLineWidth(1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }


    public static void drawItem(ItemStack itemstack, int i, int j)
    {
        final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        //TODO: FOR RENDERING BLOCKS IN PLAYERESP GuiIngame.itemRenderer.renderIcon(j, j, null, j, j);
        itemRenderer.renderItemIntoGUI(itemstack, i, j);
        itemRenderer.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, itemstack, i, j, null);
        GL11.glDisable(2884 /* GL_CULL_FACE */);
        GL11.glEnable(3008 /* GL_ALPHA_TEST */);
        GL11.glDisable(3042 /* GL_BLEND */);
        GL11.glDisable(2896 /* GL_LIGHTING */);
        GL11.glDisable(2884 /* GL_CULL_FACE */);
        GL11.glClear(256);
    }
}