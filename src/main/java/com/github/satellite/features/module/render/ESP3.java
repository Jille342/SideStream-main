package com.github.satellite.features.module.render;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventCameraTransform;
import com.github.satellite.event.listeners.EventNameTag;
import com.github.satellite.event.listeners.EventRender2D;
import com.github.satellite.features.module.Module;
import com.github.satellite.mixin.client.MixinEntityRenderer;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.Coloring;
import com.github.satellite.utils.RenderingUtils;
import com.github.satellite.utils.ServerHelper;
import com.github.satellite.utils.font.CFontRenderer;
import com.github.satellite.utils.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ESP3 extends Module {
   ModeSetting modeSetting;
    BooleanSetting ignoreInvisibleSetting;
    BooleanSetting ignoreSelfSetting;


    private final CFontRenderer tagFont = Fonts.default18;
    private final CFontRenderer healthFont = Fonts.default18;
    private final Frustum frustum = new Frustum();

    public ESP3() {
        super("ESP3", 0 ,Category.RENDER);
    }

    public void init() {
        super.init();
        this.modeSetting = new ModeSetting("Mode", "SuperCool", new String[]{"Normal", "SuperCool"});
        ignoreInvisibleSetting = new BooleanSetting("Ignore Invisibles", false);
        ignoreSelfSetting = new BooleanSetting("Ignore Self", true);
        addSetting(modeSetting,ignoreSelfSetting,ignoreInvisibleSetting);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventNameTag)
            e.cancel();
        if (e instanceof EventRender2D) {

            EntityRenderer entityRenderer = mc.entityRenderer;
            RenderManager renderManager = mc.getRenderManager();
            ScaledResolution resolution = new ScaledResolution(mc);
            int factor = resolution.getScaleFactor();
            double scaling = factor / Math.pow(factor, 2);

            GlStateManager.pushMatrix();
            GlStateManager.scale(scaling, scaling, scaling);
            for (Entity entity : mc.world.getLoadedEntityList()) {
                if (validateEntity(entity)) {
                    EntityLivingBase livingBase = (EntityLivingBase) entity;
                    double x = RenderingUtils.interpolate(entity.posX, entity.lastTickPosX, mc.getRenderPartialTicks());
                    double y = RenderingUtils.interpolate(entity.posY, entity.lastTickPosY, mc.getRenderPartialTicks());
                    double z = RenderingUtils.interpolate(entity.posZ, entity.lastTickPosZ, mc.getRenderPartialTicks());
                    double width = entity.width / 1.8;
                    double height = entity.height + (entity.isSneaking() ? -0.15 : 0.1);

                    AxisAlignedBB box = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                    Vector3d[] vectors = {
                            new Vector3d(box.minX, box.minY, box.minZ), new Vector3d(box.minX, box.maxY, box.minZ),
                            new Vector3d(box.maxX, box.minY, box.minZ), new Vector3d(box.maxX, box.maxY, box.minZ),
                            new Vector3d(box.minX, box.minY, box.maxZ), new Vector3d(box.minX, box.maxY, box.maxZ),
                            new Vector3d(box.maxX, box.minY, box.maxZ), new Vector3d(box.maxX, box.maxY, box.maxZ)
                    };
                    EventCameraTransform eventCameraTransform = new EventCameraTransform(mc.getRenderPartialTicks(), 0);
                    Satellite.onEvent(eventCameraTransform);
                                        //  entityRenderer.setupOverlayRendering(mc.getRenderPartialTicks(), 0);
                    Vector4d position = null;
                    for (Vector3d vector : vectors) {
                        vector = RenderingUtils.project2D(factor, vector.x - renderManager.viewerPosX, vector.y - renderManager.viewerPosY, vector.z - renderManager.viewerPosZ);
                        if (vector != null && vector.z >= 0 && vector.z < 1) {
                            if (position == null) {
                                position = new Vector4d(vector.x, vector.y, vector.z, 0);
                            }

                            position.x = Math.min(vector.x, position.x);
                            position.y = Math.min(vector.y, position.y);
                            position.z = Math.max(vector.x, position.z);
                            position.w = Math.max(vector.y, position.w);
                        }
                    }

                    if (position == null) continue;
                    entityRenderer.setupOverlayRendering();
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;
                    double endPosY = position.w;

                    switch (modeSetting.getMode()) {
                        case "Normal":
                            renderNormalBox(livingBase, posX, posY, endPosX, endPosY);
                            break;

                        case "SuperCool":
                            renderSuperCoolBox(livingBase, posX, posY, endPosX, endPosY);
                            break;
                    }

                    int backgroundColor = new Color(0, 0, 0, 200).getRGB();
                    double armorPercentage = livingBase.getTotalArmorValue() / 20.0;
                    double armorBarHeight = (endPosY - posY) * armorPercentage;
                    RenderingUtils.drawRect(endPosX + 1, posY - 0.5, endPosX + 3, endPosY + 0.5, backgroundColor);
                    if (armorBarHeight > 0) {
                        RenderingUtils.drawRect(endPosX + 1.5, endPosY, endPosX + 2.5D, endPosY - armorBarHeight, new Color(60, 100, 255).getRGB());
                    }

                    float health = livingBase.getHealth();
                    float maxHealth = livingBase.getMaxHealth();
                    if (health > maxHealth) {
                        health = maxHealth;
                    }

                    int healthColor = getHealthColor(health, maxHealth).getRGB();
                    double hpHeight = (endPosY - posY) * (health / maxHealth);
                    RenderingUtils.drawRect(posX - 3.5D, posY - 0.5, posX - 1.5, endPosY + 0.5, backgroundColor);
                    if (health > 0) {
                        RenderingUtils.drawRect(posX - 3, endPosY, posX - 2, endPosY - hpHeight, healthColor);
                    }

                    String tagString = livingBase.getName();
                    if (livingBase instanceof EntityPlayer) {
                        //        if (ServerHelper.isFriend((EntityPlayer) livingBase)) {
                        //          tagString += " \2477[\247aFriend\2477]";
                    }


                    String hpFormat = String.format("%.1f", health / 2 * 10);
                    healthFont.drawCenteredStringWithShadow("\2477HP: ", (posX + endPosX) / 2 - 6, posY - (healthFont.getHeight() + 2), -1);
                    healthFont.drawCenteredStringWithShadow(hpFormat.replace(".0", "") + "%", (posX + endPosX) / 2 + 6, posY - (healthFont.getHeight() + 2), healthColor);
                    tagFont.drawCenteredStringWithShadow(tagString, (posX + endPosX) / 2, posY - ((healthFont.getHeight() + 4) + tagFont.getHeight()), ServerHelper.getTeamColor(livingBase).getRGB());
                }
            }


            mc.entityRenderer.setupOverlayRendering();
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
        }
    }


    private void renderSuperCoolBox(EntityLivingBase livingBase, double posX, double posY, double endPosX, double endPosY) {
        int backgroundColor = new Color(0, 0, 0, 200).getRGB();
        Color first = Coloring.getSuperCoolColor(1, 0, 270);
        Color second = Coloring.getSuperCoolColor(1, 90, 270);
        Color third = Coloring.getSuperCoolColor(1, 180, 270);
        Color fourth = Coloring.getSuperCoolColor(1, 270, 270);
        if (livingBase.hurtTime > 0) {
            first = new Color(255, 80, 80);
            second = new Color(255, 80, 80);
            third = new Color(255, 80, 80);
            fourth = new Color(255, 80, 80);
        }

        RenderingUtils.drawRect(posX - 1, posY, posX + 1, endPosY + 0.5, backgroundColor);
        RenderingUtils.drawRect(posX - 1, endPosY - 1.5, endPosX + 0.5, endPosY + 0.5, backgroundColor);
        RenderingUtils.drawRect(endPosX - 1.5, posY, endPosX + 0.5, endPosY + 0.5, backgroundColor);
        RenderingUtils.drawRect(posX - 1, posY - 0.5, endPosX + 0.5, posY + 1.5, backgroundColor);

        RenderingUtils.drawGradientH(posX - 0.5, posY, posX + 0.5, endPosY, first.getRGB(), second.getRGB());
        RenderingUtils.drawGradientV(posX, endPosY - 1, endPosX, endPosY, second.getRGB(), third.getRGB());
        RenderingUtils.drawGradientH(endPosX - 1, posY, endPosX, endPosY, fourth.getRGB(), third.getRGB());
        RenderingUtils.drawGradientV(posX - 0.5, posY, endPosX, posY + 1, first.getRGB(), fourth.getRGB());
    }

    private void renderNormalBox(EntityLivingBase livingBase, double posX, double posY, double endPosX, double endPosY) {
        int backgroundColor = new Color(0, 0, 0, 200).getRGB();
        int mainColor = new Color(40, 255, 60).getRGB();
        if (livingBase.hurtTime > 0) {
            mainColor = new Color(255, 80, 80).getRGB();
        }

        RenderingUtils.drawRect(posX + 0.5, posY, posX - 1, posY + (endPosY - posY) / 7 + 0.5, backgroundColor);
        RenderingUtils.drawRect(posX - 1, endPosY, posX + 0.5, endPosY - (endPosY - posY) / 7 - 0.5, backgroundColor);
        RenderingUtils.drawRect(posX - 1, posY - 0.5, posX + (endPosX - posX) / 4 + 0.5, posY + 1, backgroundColor);
        RenderingUtils.drawRect(endPosX - (endPosX - posX) / 4 - 0.5, posY - 0.5, endPosX, posY + 1, backgroundColor);
        RenderingUtils.drawRect(endPosX - 1, posY, endPosX + 0.5, posY + (endPosY - posY) / 7 + 0.5, backgroundColor);
        RenderingUtils.drawRect(endPosX - 1, endPosY, endPosX + 0.5, endPosY - (endPosY - posY) / 7 - 0.5, backgroundColor);
        RenderingUtils.drawRect(posX - 1, endPosY - 1, posX + (endPosX - posX) / 4 + 0.5, endPosY + 0.5, backgroundColor);
        RenderingUtils.drawRect(endPosX - (endPosX - posX) / 4 - 0.5, endPosY - 1, endPosX + 0.5, endPosY + 0.5, backgroundColor);

        RenderingUtils.drawRect(posX, posY, posX - 0.5, posY + (endPosY - posY) / 7, mainColor);
        RenderingUtils.drawRect(posX, endPosY, posX - 0.5, endPosY - (endPosY - posY) / 7, mainColor);
        RenderingUtils.drawRect(posX - 0.5, posY, posX + (endPosX - posX) / 4, posY + 0.5, mainColor);
        RenderingUtils.drawRect(endPosX - (endPosX - posX) / 4, posY, endPosX, posY + 0.5, mainColor);
        RenderingUtils.drawRect(endPosX - 0.5, posY, endPosX, posY + (endPosY - posY) / 7, mainColor);
        RenderingUtils.drawRect(endPosX - 0.5, endPosY, endPosX, endPosY - (endPosY - posY) / 7, mainColor);
        RenderingUtils.drawRect(posX, endPosY - 0.5, posX + (endPosX - posX) / 4, endPosY, mainColor);
        RenderingUtils.drawRect(endPosX - (endPosX - posX) / 4, endPosY - 0.5, endPosX - 0.5, endPosY, mainColor);
    }

    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = {
                0.0F,
                0.5F,
                1.0F
        };
        Color[] healthColors = {
                new Color(255, 50, 50),
                new Color(255, 180, 50),
                new Color(50, 255, 50)
        };
        return RenderingUtils.blendColors(fractions, healthColors, health / maxHealth).brighter();
    }

    private boolean validateEntity(Entity entity) {
        if ((ignoreSelfSetting.enable && entity == mc.player) || (ignoreInvisibleSetting.enable && entity.isInvisible()) || (entity == mc.player && mc.gameSettings.thirdPersonView == 0)) {
            return false;
        }
        return !entity.isDead && entity instanceof EntityPlayer && isInViewFrustum(entity.getEntityBoundingBox());
    }

    private boolean isInViewFrustum(AxisAlignedBB bb) {
        frustum.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
        return frustum.isBoundingBoxInFrustum(bb);
    }

}
