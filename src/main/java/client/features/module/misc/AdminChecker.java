package client.features.module.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import client.features.module.Module;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import client.utils.font.TTFFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.util.math.BlockPos;

public class AdminChecker extends Module {
    private int lastAdmins;

    private final ArrayList<String> admins;

    private final TimeHelper timer;
    NumberSetting delay;
    private final TimeHelper timer2 = new TimeHelper();

    private String adminname;

    private final CFontRenderer font = Fonts.default18;

    public AdminChecker() {
        super("AdminChecker",  0, Category.MISC);
        this.admins = new ArrayList<>();
        this.timer = new TimeHelper();
    }
    public void init() {
        this.delay = new NumberSetting("Chat Delay", 1000, 1000, 5000, 1000F);
        addSetting(delay); super.init();

    }


    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            if (!this.admins.isEmpty()) {
                font.drawStringWithShadow("" + String.valueOf(this.admins.size()), ((new ScaledResolution(mc)).getScaledWidth() / 2 - mc.fontRenderer.getStringWidth("" + String.valueOf(this.admins.size())) + 20), ((new ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
            } else {
                font.drawStringWithShadow("Admins: " + String.valueOf(this.admins.size()), ((new ScaledResolution(mc)).getScaledWidth() / 2 - mc.fontRenderer.getStringWidth("Admins: " + String.valueOf(this.admins.size())) + 20), ((new ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
            }
        }
        if (e instanceof EventUpdate) {
            if (this.timer.hasReached(5000.0F)) {
                this.timer.reset();
                mc.player.connection.sendPacket(new CPacketTabComplete("/vanishnopacket:vanish ", BlockPos.ORIGIN, false));
                mc.player.connection.sendPacket(new CPacketTabComplete("/rank ", BlockPos.ORIGIN, false));
            }
            setTag(String.valueOf(admins.size()));
            if (!this.admins.isEmpty())
                displayAdmins();
        }
        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket) e);
            if (event.getPacket()instanceof SPacketTabComplete) {
                SPacketTabComplete packet = (SPacketTabComplete) event.getPacket();
                this.admins.clear();
                String[] matches;
                for (int length = (matches = packet.getMatches()).length, i = 0; i < length; i++) {
                    String user = matches[i];
                    String[] administrators;
                    for (int length2 = (administrators = getAdministrators()).length, j = 0; j < length2; j++) {
                        String admin = administrators[j];
                        if (user.equalsIgnoreCase(admin)) {
                            adminname = user;
                            displayAdmins();
                            this.admins.add(user);
                        }
                    }
                }
                this.lastAdmins = this.admins.size();
            } else if (event.getPacket() instanceof SPacketPlayerListItem) {
                SPacketPlayerListItem packetPlayInPlayerListItem = (SPacketPlayerListItem) event.getPacket();
                if (packetPlayInPlayerListItem.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY)
                    for (SPacketPlayerListItem.AddPlayerData addPlayerData : packetPlayInPlayerListItem.getEntries()) {
                        if (mc.getConnection().getPlayerInfo(addPlayerData.getProfile().getId()) == null) {
                            String name = getName(addPlayerData.getProfile().getId());
                            if (Objects.isNull(name)) {
                                checkList("NullPlayer");
                                continue;
                            }
                            if (Arrays.toString((Object[]) getAdministrators()).contains(name))
                                checkList(name);
                        }
                    }
            }
        }
    }

    public void displayAdmins() {

        if (timer2.hasReached(delay.value)) {
            ChatUtils.printChat(String.valueOf("INC " + admins + " " + admins.size()));
            timer2.reset();
        }
    }

    public String[] getAdministrators() {
        return new String[] {
                "ACrispyTortilla",
                "ArcticStorm141",
                "ArsMagia",
                "Captainbenedict",
                "Carrots386",
                "DJ_Pedro",
                "DocCodeSharp",
                "FullAdmin",
                "Galap",
                "HighlifeTTU",
                "ImbC",
                "InstantLightning",
                "JTGangsterLP6",
                "Kevin_is_Panda",
                "Kingey",
                "Marine_PvP",
                "MissHilevi",
                "Mistri",
                "Mosh_Von_Void",
                "Navarr",
                "PokeTheEye",
                "Rafiki2085",
                "Robertthegoat",
                "Sevy13",
                "andrew323",
                "dLeMoNb",
                "lazertester",
                "noobfan",
                "skillerfox3",
                "storm345",
                "windex_07",
                "AlecJ",
                "JACOBSMILE",
                "Wayvernia",
                "gunso_",
                "Hughzaz",
                "Murgatron",
                "SaxaphoneWalrus",
                "_Ahri",
                "SakuraWolfVeghetto",
                "SnowVi1liers",
                "jiren74",
                "Dange",
                "Tatre",
                "Pichu2002",
                "LegendaryAlex",
                "LaukNLoad",
                "M4bi",
                "HellionX2",
                "Ktrompfl",
                "Bupin",
                "Murgatron",
                "Outra",
                "CoastinJosh",
                "sabau",
                "Axyy",
                "lPirlo",
                "ImAbbyy" };
    }

    public ArrayList<String> getAdmins() {
        ArrayList<String> admins = new ArrayList<>();
        if (mc.getConnection().getPlayerInfoMap() != null)
            for (NetworkPlayerInfo player : mc.getConnection().getPlayerInfoMap()) {
                String text = player.getGameProfile().getName();
                admins.add(text);
            }
        return admins;
    }

    public String getName(UUID uuid) {
        return ServerHelper.getName(uuid);
    }

    private void checkList(String uuid) {
        if (this.admins.contains(uuid))
            return;
        this.admins.add(uuid);
    }
}
