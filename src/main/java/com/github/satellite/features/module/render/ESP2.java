package com.github.satellite.features.module.render;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.*;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.features.module.render.NameTags;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.Colors;
import com.github.satellite.utils.RenderingUtils;
import com.github.satellite.utils.ServerHelper;
import com.github.satellite.utils.font.CFontRenderer;
import com.github.satellite.utils.font.Fonts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ESP2 extends Module {
    public final List<Entity> collectedEntities = new ArrayList<>();
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    private int color = Color.WHITE.getRGB();
    private final int backgroundColor = (new Color(0, 0, 0, 120)).getRGB();
    private final int black = Color.BLACK.getRGB();

   BooleanSetting armor;
   BooleanSetting tags;
   BooleanSetting health;
   BooleanSetting items;
   BooleanSetting players;
   BooleanSetting invisibles;
   BooleanSetting animals;
   BooleanSetting border;
   BooleanSetting monsters;

    ModeSetting mode;
    ModeSetting mode2d;
    ModeSetting colormode;
    public ESP2() {
        super("ESP2", 0,Category.RENDER);

    }
    @Override
    public void init() {
        super.init();
        armor = new BooleanSetting("Armor", true);
        tags = new BooleanSetting("Tags", true);
        health = new BooleanSetting("Show Health", true);
        items = new BooleanSetting("Items", true);
        players = new BooleanSetting("Players", true);
        invisibles = new BooleanSetting("Invisibles", true);
        animals = new BooleanSetting("Animals", true);
        border = new BooleanSetting("Border", true);
        monsters = new BooleanSetting("Monsters", true);
        this.mode = new ModeSetting("Mode", "2D", new String[] {"2D", "Box"});
        this.colormode = new ModeSetting("Colormode", "Shotbow", new String[] {"Rainbow", "Team", "Shotbow"});
        this.mode2d = new ModeSetting("Mode2d", "Box", new String[] {"Box", "Corner", "Apex"});
        addSetting(armor, tags,health,items,players,invisibles,animals,border,monsters,mode,colormode,mode2d);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventRenderGUI) {
          ScaledResolution scaledResolution = new ScaledResolution(mc);
            switch (mode.getMode()) {
                case "2D": {
                    GL11.glPushMatrix();
                    collectEntities();
                    float partialTicks = mc.getRenderPartialTicks();
                    int scaleFactor = scaledResolution.getScaleFactor();
                    double scaling = scaleFactor / Math.pow(scaleFactor, 2.0D);
                    GL11.glScaled(scaling, scaling, scaling);
                    int black = this.black;
                    int color = this.color;
                    int background = this.backgroundColor;
                    float scale = 0.65F;
                    float upscale = 1.0F / scale;
                    FontRenderer fr = mc.fontRenderer;
                    CFontRenderer font1 = Fonts.default12;
                    CFontRenderer font2 = Fonts.default20;
                    CFontRenderer font3 = Fonts.default10;
                    RenderManager renderMng = mc.getRenderManager();
                    EntityRenderer entityRenderer = mc.entityRenderer;
                    String mode = mode2d.getMode();
                    List<Entity> collectedEntities = this.collectedEntities;

                    for (int i = 0, collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; i++) {
                        Entity entity = collectedEntities.get(i);
                        if (isValid(entity) && RenderingUtils.isInViewFrustrum(entity)) {
                            double x = RenderingUtils.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                            double y = RenderingUtils.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                            double z = RenderingUtils.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                            double width = entity.width / 1.5D;
                            double height = entity.height + ((entity.isSneaking() ) ? -0.3D : 0.2D);
                            AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                            Vector3d[] vectors = new Vector3d[]{new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)};
                           EventCameraTransform eventCameraTransform = new EventCameraTransform();
                            Satellite.onEvent(eventCameraTransform);

                            Vector4d position = null;
                            for (Vector3d vector : vectors) {
                                vector = project2D(scaleFactor, vector.x - renderMng.viewerPosX, vector.y - renderMng.viewerPosY, vector.z - renderMng.viewerPosZ);
                                if (vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
                                    if (position == null)
                                        position = new Vector4d(vector.x, vector.y, vector.z, 0.0D);
                                    position.x = Math.min(vector.x, position.x);
                                    position.y = Math.min(vector.y, position.y);
                                    position.z = Math.max(vector.x, position.z);
                                    position.w = Math.max(vector.y, position.w);
                                }
                            }

                            if (position != null) {
                                entityRenderer.setupOverlayRendering();
                                double posX = position.x;
                                double posY = position.y;
                                double endPosX = position.z;
                                double endPosY = position.w;
                                if (border.enable) {
                                    switch (mode) {
                                        case "Box":
                                            RenderingUtils.drawRect(posX - 1.0D, posY, posX + 0.5D, endPosY + 0.5D, black);
                                            RenderingUtils.drawRect(posX - 1.0D, posY - 0.5D, endPosX + 0.5D, posY + 0.5D + 0.5D, black);
                                            RenderingUtils.drawRect(endPosX - 0.5D - 0.5D, posY, endPosX + 0.5D, endPosY + 0.5D, black);
                                            RenderingUtils.drawRect(posX - 1.0D, endPosY - 0.5D - 0.5D, endPosX + 0.5D, endPosY + 0.5D, black);
                                            RenderingUtils.drawRect(posX - 0.5D, posY, posX + 0.5D - 0.5D, endPosY, color);
                                            RenderingUtils.drawRect(posX, endPosY - 0.5D, endPosX, endPosY, color);
                                            RenderingUtils.drawRect(posX - 0.5D, posY, endPosX, posY + 0.5D, color);
                                            RenderingUtils.drawRect(endPosX - 0.5D, posY, endPosX, endPosY, color);
                                            break;
                                        case "Corner":
                                            RenderingUtils.drawRect(posX + 0.5D, posY, posX - 1.0D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                                            RenderingUtils.drawRect(posX - 1.0D, endPosY, posX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                                            RenderingUtils.drawRect(posX - 1.0D, posY - 0.5D, posX + (endPosX - posX) / 3.0D + 0.5D, posY + 1.0D, black);
                                            RenderingUtils.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, posY - 0.5D, endPosX, posY + 1.0D, black);
                                            RenderingUtils.drawRect(endPosX - 1.0D, posY, endPosX + 0.5D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                                            RenderingUtils.drawRect(endPosX - 1.0D, endPosY, endPosX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                                            RenderingUtils.drawRect(posX - 1.0D, endPosY - 1.0D, posX + (endPosX - posX) / 3.0D + 0.5D, endPosY + 0.5D, black);
                                            RenderingUtils.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, endPosY - 1.0D, endPosX + 0.5D, endPosY + 0.5D, black);
                                            RenderingUtils.drawRect(posX, posY, posX - 0.5D, posY + (endPosY - posY) / 4.0D, color);
                                            RenderingUtils.drawRect(posX, endPosY, posX - 0.5D, endPosY - (endPosY - posY) / 4.0D, color);
                                            RenderingUtils.drawRect(posX - 0.5D, posY, posX + (endPosX - posX) / 3.0D, posY + 0.5D, color);
                                            RenderingUtils.drawRect(endPosX - (endPosX - posX) / 3.0D, posY, endPosX, posY + 0.5D, color);
                                            RenderingUtils.drawRect(endPosX - 0.5D, posY, endPosX, posY + (endPosY - posY) / 4.0D, color);
                                            RenderingUtils.drawRect(endPosX - 0.5D, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0D, color);
                                            RenderingUtils.drawRect(posX, endPosY - 0.5D, posX + (endPosX - posX) / 3.0D, endPosY, color);
                                            RenderingUtils.drawRect(endPosX - (endPosX - posX) / 3.0D, endPosY - 0.5D, endPosX - 0.5D, endPosY, color);
                                            break;
                                        case "Apex":
                                            RenderingUtils.drawRect(endPosX - .5 - .5, posY, endPosX + .5, endPosY + .5, black);
                                            RenderingUtils.drawRect(posX - 1, posY, posX + .5, endPosY + .5, black);

                                            RenderingUtils.drawRect(posX - 1, endPosY - 1, posX + (endPosX - posX) / 4 + .5, endPosY + .5, black);
                                            RenderingUtils.drawRect(endPosX - 1, endPosY - 1, endPosX + (posX - endPosX) / 4 - .5, endPosY + .5, black);
                                            RenderingUtils.drawRect(posX - 1, posY - .5, posX + (endPosX - posX) / 4 + .5, posY + 1, black);
                                            RenderingUtils.drawRect(endPosX, posY - .5, endPosX + (posX - endPosX) / 4 - .5, posY + 1, black);

                                            RenderingUtils.drawRect(posX - .5, posY, posX + .5 - .5, endPosY, color);
                                            RenderingUtils.drawRect(endPosX - .5, posY, endPosX, endPosY, color);

                                            RenderingUtils.drawRect(posX, endPosY - .5, posX + (endPosX - posX) / 4, endPosY, color);
                                            RenderingUtils.drawRect(endPosX, endPosY - .5, endPosX + (posX - endPosX) / 4, endPosY, color);
                                            RenderingUtils.drawRect(posX - .5, posY, posX + (endPosX - posX) / 4, posY + .5, color);
                                            RenderingUtils.drawRect(endPosX, posY, endPosX + (posX - endPosX) / 4, posY + .5, color);
                                            break;
                                    }
                                }
                                boolean living = entity instanceof EntityLivingBase;
                                if (living) {
                                    EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                                    if (health.enable) {
                                        float hp = entityLivingBase.getHealth();
                                        float maxHealth = entityLivingBase.getMaxHealth();
                                        if (hp > maxHealth)
                                            hp = maxHealth;
                                        double hpPercentage = (hp / maxHealth);
                                        double hpHeight = (endPosY - posY) * hpPercentage;
                                        RenderingUtils.drawRect(posX - 3.5D, posY - 0.5D, posX - 1.5D, endPosY + 0.5D, background);
                                        if (hp > 0.0F) {
                                            float absorption = entityLivingBase.getAbsorptionAmount();
                                            int healthColor = Colors.getHealthColor(hp, maxHealth).getRGB();
                                            RenderingUtils.drawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - hpHeight, healthColor);
                                            if (mc.player.getDistanceSq(entityLivingBase) < 20)
                                                font3.drawStringWithShadow("" + MathHelper.floor(hp), posX - 8.0D, endPosY - hpHeight, healthColor);
                                            if (absorption > 0.0F)
                                                RenderingUtils.drawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - (endPosY - posY) / 6.0D * absorption / 2.0D, new Color(255, 215, 77, 150).getRGB());
                                        }
                                    }
                                }
                                if (tags.enable && !(ModuleManager.getModulebyClass(NameTags.class).isEnable())) {
                                    float scaledHeight = 10.0F;
                                    int texcolor = -1;
                                    String name = entity.getName();
                                    if (entity instanceof EntityItem) {
                                        name = ((EntityItem) entity).getItem().getDisplayName();
                                    }
                                    String prefix = "";
                                    switch (colormode.getMode()) {
                                        case "Rainbow":
                                            texcolor = Colors.rainbow(1400, 1.0F, 1.0F);
                                            break;
                                        case "Team":
                                     //       texcolor = ServerHelper.isTeammate((EntityPlayer) entity) ? Colors.getColor(255, 60, 60) : Colors.getColor(60, 255, 60);
                                            break;
                                        case "Shotbow":
                                            texcolor = Colors.getTeamColor((EntityPlayer) entity);
                                            break;
                                    }

                                    double dif = (endPosX - posX) / 2.0D;
                                    double textWidth = (font2.getStringWidth(name + " §7" + (int) mc.player.getDistanceSq(entity) + "m") * scale);
                                    float tagX = (float) ((posX + dif - textWidth / 2.0D) * upscale);
                                    float tagY = (float) (posY * upscale) - scaledHeight;
                                    GL11.glPushMatrix();
                                    GL11.glScalef(scale, scale, scale);
                                    if (living)
                                        RenderingUtils.drawRect((tagX - 2.0F), (tagY - 2.0F), tagX + textWidth * upscale + 2.0D, (tagY + 9.0F), (new Color(0, 0, 0, 140)).getRGB());
                                    font2.drawStringWithShadow(prefix + name + " §7" + (int) mc.player.getDistanceSq(entity) + "m", tagX, tagY, texcolor);
                                    GL11.glPopMatrix();
                                }
                                if (armor.enable) {
                                    if (living) {
                                        if (entity instanceof EntityPlayer) {
                                            EntityPlayer player = (EntityPlayer) entity;
                                            double ydiff = (endPosY - posY) / 4;

                                            ItemStack stack = (player).inventory.getStackInSlot(4);
                                            if (stack != null) {
                                                RenderingUtils.drawRect(endPosX + 3.5D, posY - 0.5D, endPosX + 1.5D, posY + ydiff, background);
                                                double diff1 = (posY + ydiff - 1) - (posY + 2);
                                                double percent = 1 - (double) stack.getItemDamage() / (double) stack.getMaxDamage();
                                                RenderingUtils.drawRect(endPosX + 3.0D, posY + ydiff, endPosX + 2.0D, posY + ydiff - 3.0D - (diff1 * percent), new Color(78, 206, 229).getRGB());

                                                String stackname = (stack.getDisplayName().equalsIgnoreCase("Air")) ? "0" : !(stack.getItem() instanceof ItemArmor) ? stack.getDisplayName() : stack.getMaxDamage() - stack.getItemDamage() + "";
                                                if (mc.player.getDistanceSq(player) < 20) {
//                                        mc.getRenderItem().renderItemIntoGUI(stack, endPosX + 4, posY + ydiff - 1 - (diff1 / 2) - 18);
                                                    font1.drawStringWithShadow(stackname, (float) endPosX + 5, (float) (posY + ydiff - 1 - (diff1 / 2)) - (font1.getStringHeight(stack.getMaxDamage() - stack.getItemDamage() + "") / 2), -1);
                                                }
                                            }
                                            ItemStack stack2 = (player).inventory.getStackInSlot(3);
                                            if (stack2 != null) {
                                                RenderingUtils.drawRect(endPosX + 3.5D, posY + ydiff, endPosX + 1.5D, posY + ydiff * 2, background);
                                                double diff1 = (posY + ydiff * 2) - (posY + ydiff + 2);
                                                double percent = 1 - (double) stack2.getItemDamage() * 1 / (double) stack2.getMaxDamage();
                                                RenderingUtils.drawRect(endPosX + 3.0D, (posY + ydiff * 2), endPosX + 2.0D, (posY + ydiff * 2) - 1.0D - (diff1 * percent), new Color(78, 206, 229).getRGB());

                                                String stackname = (stack.getDisplayName().equalsIgnoreCase("Air")) ? "0" : !(stack2.getItem() instanceof ItemArmor) ? stack2.getDisplayName() : stack2.getMaxDamage() - stack2.getItemDamage() + "";
                                                if (mc.player.getDistanceSq(player) < 20) {
//                                        mc.getRenderItem().renderItemIntoGUI(stack2, endPosX + 4, (posY + ydiff * 2) - (diff1 / 2) - 18);
                                                    font1.drawStringWithShadow(stackname, (float) endPosX + 5, (float) ((posY + ydiff * 2) - (diff1 / 2)) - (font1.getStringHeight(stack2.getMaxDamage() - stack2.getItemDamage() + "") / 2), -1);
                                                }
                                            }
                                            ItemStack stack3 = (player).inventory.getStackInSlot(2);
                                            if (stack3 != null) {
                                                RenderingUtils.drawRect(endPosX + 3.5D, posY + ydiff * 2, endPosX + 1.5D, posY + ydiff * 3, background);
                                                double diff1 = (posY + ydiff * 3) - (posY + ydiff * 2 + 2);
                                                double percent = 1 - (double) stack3.getItemDamage() * 1 / (double) stack3.getMaxDamage();
                                                RenderingUtils.drawRect(endPosX + 3.0D, (posY + ydiff * 3), endPosX + 2.0D, (posY + ydiff * 3) - 1.0D - (diff1 * percent), new Color(78, 206, 229).getRGB());

                                                String stackname = (stack.getDisplayName().equalsIgnoreCase("Air")) ? "0" : !(stack3.getItem() instanceof ItemArmor) ? stack3.getDisplayName() : stack3.getMaxDamage() - stack3.getItemDamage() + "";
                                                if (mc.player.getDistanceSq(player) < 20) {
//                                        mc.getRenderItem().renderItemIntoGUI(stack3, endPosX + 4, (posY + ydiff * 3) - (diff1 / 2) - 18);
                                                    font1.drawStringWithShadow(stackname, (float) endPosX + 5, (float) ((posY + ydiff * 3) - (diff1 / 2)) - (font1.getStringHeight(stack3.getMaxDamage() - stack3.getItemDamage() + "") / 2), -1);
                                                }
                                            }
                                            ItemStack stack4 = (player).inventory.getStackInSlot(1);
                                            if (stack4 != null) {
                                                RenderingUtils.drawRect(endPosX + 3.5D, posY + ydiff * 3, endPosX + 1.5D, posY + ydiff * 4 + 0.5D, background);
                                                double diff1 = (posY + ydiff * 4) - (posY + ydiff * 3 + 2);
                                                double percent = 1 - (double) stack4.getItemDamage() * 1 / (double) stack4.getMaxDamage();
                                                RenderingUtils.drawRect(endPosX + 3.0D, (posY + ydiff * 4), endPosX + 2.0D, (posY + ydiff * 4) - 1.0D - (diff1 * percent), new Color(78, 206, 229).getRGB());

                                                String stackname = (stack.getDisplayName().equalsIgnoreCase("Air")) ? "0" : !(stack4.getItem() instanceof ItemArmor) ? stack4.getDisplayName() : stack4.getMaxDamage() - stack4.getItemDamage() + "";
                                                if (mc.player.getDistanceSq(player) < 20) {
//                                        mc.getRenderItem().renderItemIntoGUI(stack4, endPosX + 4, (posY + ydiff * 4) - (diff1 / 2) - 18);
                                                    font1.drawStringWithShadow(stackname, (float) endPosX + 5, (float) ((posY + ydiff * 4) - (diff1 / 2)) - (font1.getStringHeight(stack4.getMaxDamage() - stack4.getItemDamage() + "") / 2), -1);
                                                }
                                            }
                                        }
                                    } else if (entity instanceof EntityItem) {
                                        ItemStack itemStack = ((EntityItem) entity).getItem();
                                        if (itemStack.isItemStackDamageable()) {
                                            int maxDamage = itemStack.getMaxDamage();
                                            float itemDurability = (maxDamage - itemStack.getItemDamage());
                                            double durabilityWidth = (endPosX - posX) * itemDurability / maxDamage;
                                            RenderingUtils.drawRect(posX - 0.5D, endPosY + 1.5D, posX - 0.5D + endPosX - posX + 1.0D, endPosY + 1.5D + 2.0D, background);
                                            RenderingUtils.drawRect(posX, endPosY + 2.0D, posX + durabilityWidth, endPosY + 3.0D, 16777215);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    GL11.glPopMatrix();
                    GlStateManager.enableBlend();
                    entityRenderer.setupOverlayRendering();
                }
                break;
            }
        }
        if(e instanceof  EventRenderWorld) {
            String currentmode = mode.getMode();

            switch (currentmode) {
                case "Box":
                    for (EntityPlayer entity : mc.world.playerEntities) {
                        if (isValid(entity)) {
                            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks() - mc.getRenderManager().viewerPosX;
                            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - mc.getRenderManager().viewerPosY;
                            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks() - mc.getRenderManager().viewerPosZ;
                            double widthX = (entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) / 2 + 0.05;
                            double widthZ = (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) / 2 + 0.05;
                            double height = (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY);

                            switch (colormode.getMode()) {
                                case "Rainbow":
                                    this.color = Colors.rainbow(1400, 1.0F, 1.0F);
                                    break;
                                case "Team":
                          //      this.color = ServerHelper.isTeammate((EntityPlayer) entity) ? Colors.getColor(255, 60, 60) : Colors.getColor(60, 255, 60);
                                    break;
                                case "Shotbow":
                                   this.color = Colors.getTeamColor(entity);
                                    break;
                            }

                            if (entity instanceof EntityPlayer)
                                height *= 1.1;

                            RenderingUtils.pre3D();
                            RenderingUtils.glColor(this.color);
                            for (int i = 0; i < 2; i++) {
                                GL11.glLineWidth(1);
                                GL11.glBegin(GL11.GL_LINE_STRIP);
                                GL11.glVertex3d(x - widthX, y, z - widthZ);
                                GL11.glVertex3d(x - widthX, y, z - widthZ);
                                GL11.glVertex3d(x - widthX, y + height, z - widthZ);
                                GL11.glVertex3d(x + widthX, y + height, z - widthZ);
                                GL11.glVertex3d(x + widthX, y, z - widthZ);
                                GL11.glVertex3d(x - widthX, y, z - widthZ);
                                GL11.glVertex3d(x - widthX, y, z + widthZ);
                                GL11.glEnd();
                                GL11.glBegin(GL11.GL_LINE_STRIP);
                                GL11.glVertex3d(x + widthX, y, z + widthZ);
                                GL11.glVertex3d(x + widthX, y + height, z + widthZ);
                                GL11.glVertex3d(x - widthX, y + height, z + widthZ);
                                GL11.glVertex3d(x - widthX, y, z + widthZ);
                                GL11.glVertex3d(x + widthX, y, z + widthZ);
                                GL11.glVertex3d(x + widthX, y, z - widthZ);
                                GL11.glEnd();
                                GL11.glBegin(GL11.GL_LINE_STRIP);
                                GL11.glVertex3d(x + widthX, y + height, z + widthZ);
                                GL11.glVertex3d(x + widthX, y + height, z - widthZ);
                                GL11.glEnd();
                                GL11.glBegin(GL11.GL_LINE_STRIP);
                                GL11.glVertex3d(x - widthX, y + height, z + widthZ);
                                GL11.glVertex3d(x - widthX, y + height, z - widthZ);
                                GL11.glEnd();
                            }
                            RenderingUtils.post3D();
                        }
                    }
                    break;
            }
        }
        if (e instanceof EventNameTag) {
            if ((Boolean) tags.enable && (mode.getMode() == "2D")) {
                e.setCancelled(true);
            }
        }
    }
    private void collectEntities() {
        this.collectedEntities.clear();
        List<EntityPlayer> playerEntities = mc.world.playerEntities;
        for (int i = 0, playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; i++) {
            Entity entity = playerEntities.get(i);
            if (isValid(entity))
                this.collectedEntities.add(entity);
        }
    }
    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        if (GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.vector))
            return new Vector3d((this.vector.get(0) / scaleFactor), ((Display.getHeight() - this.vector.get(1)) / scaleFactor), this.vector.get(2));
        return null;
    }
    private boolean isValid(Entity entity) {
        if (entity == mc.player && (mc.gameSettings.thirdPersonView == 0))
            return false;
        if (entity.isDead)
            return false;
        if (!(Boolean) invisibles.enable && entity.isInvisible())
            return false;
        if ((Boolean) items.enable && entity instanceof EntityItem && mc.player.getDistanceSq(entity) < 10.0F)
            return true;
        if ((Boolean) animals.enable && entity instanceof net.minecraft.entity.passive.EntityAnimal)
            return true;
        if ((Boolean) players.enable&& entity instanceof EntityPlayer)
            return true;
        return ((Boolean) monsters.enable && (entity instanceof net.minecraft.entity.monster.EntityMob || entity instanceof net.minecraft.entity.monster.EntitySlime || entity instanceof net.minecraft.entity.boss.EntityDragon || entity instanceof net.minecraft.entity.monster.EntityGolem));
    }
}
