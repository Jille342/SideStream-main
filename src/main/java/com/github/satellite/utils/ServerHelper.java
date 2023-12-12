package com.github.satellite.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerHelper {

    private static Map<UUID, String> nameCache = new HashMap<>();
    private String name;
    private static Map<String, UUID> uuidCache = new HashMap<>();
    private static Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    public static Color getTeamColor(Entity entityIn) {
        Color color = Color.WHITE;
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) entityIn.getTeam();
        if (scoreplayerteam != null) {
            String s = FontRenderer.getFormatFromString(scoreplayerteam.getPrefix());
            if (!Strings.isNullOrEmpty(s) && s.length() >= 2) {
                color = new Color(Minecraft.getMinecraft().fontRenderer.getColorCode(s.charAt(1)));
            }
        }
        return color;
    }
    public static int getTeamColor(EntityPlayer player) {
        int color = 16777215;
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)player.getTeam();
        if (scoreplayerteam != null) {
            String s = FontRenderer.getFormatFromString(scoreplayerteam.getPrefix());
            if (s.length() >= 2)
                color = Minecraft.getMinecraft().fontRenderer.getColorCode(s.charAt(1));
        }
        return color;
    }

    public static String getName(UUID uuid) {
        if (nameCache.containsKey(uuid))
            return nameCache.get(uuid);
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(String.format("https://api.mojang.com/user/profiles/%s/names", new Object[] { UUIDTypeAdapter.fromUUID(uuid) }))).openConnection();
            httpURLConnection.setReadTimeout(5000);
            ServerHelper[] array = (ServerHelper[])gson.fromJson(new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())), ServerHelper[].class);
            ServerHelper uuidFetcher = array[array.length - 1];
            uuidCache.put(uuidFetcher.name.toLowerCase(), uuid);
            nameCache.put(uuid, uuidFetcher.name);
            return uuidFetcher.name;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isTeammate(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean result = false;
        TextFormatting formatting = null;
        TextFormatting formatting2 = null;
        if (player != null) {
            for (TextFormatting textFormatting : TextFormatting.values()) {
                if (textFormatting != TextFormatting.RESET) {
                    if (mc.player.getDisplayName().getFormattedText().contains(textFormatting.toString()) && formatting == null) {
                        formatting = textFormatting;
                    }
                    if (player.getDisplayName().getFormattedText().contains(textFormatting.toString()) && formatting2 == null) {
                        formatting2 = textFormatting;
                    }
                }
            }

            if (formatting != null && formatting2 != null) {
                result = (formatting == formatting2);
            } else if (mc.player.getTeam() != null) {
                result = mc.player.isOnSameTeam(player);
            } else if (mc.player.inventory.armorInventory.get(3).getItem() instanceof ItemBlock) {
                result = ItemStack.areItemStacksEqual(mc.player.inventory.armorInventory.get(3), player.inventory.armorInventory.get(3));
            }
        }
        return result;
    }

   // public static boolean isFriend(EntityPlayer player) {
     //   return FriendRegistry.getFriends().stream().anyMatch(ign -> ign.equals(player.getName()));
    //}
}
