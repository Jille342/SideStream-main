package client.utils;

import client.Client;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;

public class ChatUtils implements MCUtil {
    public final static String chatPrefix = "\2477[\2476Ex\2477] \2478>> \247f";
    public final static String ircchatPrefix = "\2477[\2476Ex\2479IRC\2477] \247f";

    public static void printChat(String text) {

            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("["+ Client.NAME+ "] "+text));

    }


    public static void printChatNoName(String text) {

        mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(text));

    }
    public static void printChatprefix(String text) {
        mc.player.sendChatMessage(String.valueOf(new TextComponentString(chatPrefix + text)));
    }

    public static void printIRCChatprefix(String text) {
        mc.player.sendChatMessage(String.valueOf(new TextComponentString(ircchatPrefix + text)));
    }

    public static void sendChat_NoFilter(String text) {
        mc.player.connection.sendPacket(new CPacketChatMessage(text));
    }

    public static void sendChat(String text) {
        mc.player.sendChatMessage(text);
    }
}