package wtf.mania.module.impl.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventRender2D;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.ServerHelper;
import com.github.satellite.utils.TimeHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.util.math.BlockPos;

public class AdminChecker extends Module {
    private int lastAdmins;

    private final ArrayList<String> admins;

    private final TimeHelper timer;

    public AdminChecker() {
        super("AdminChecker",  0, Category.MISC);
        this.admins = new ArrayList<>();
        this.timer = new TimeHelper();
    }


    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            if (this.admins.size() > 0) {
                mc.fontRenderer.drawStringWithShadow("" + String.valueOf(this.admins.size()), ((new ScaledResolution(mc)).getScaledWidth() / 2 - mc.fontRenderer.getStringWidth("" + String.valueOf(this.admins.size())) + 20), ((new ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
            } else {
                mc.fontRenderer.drawStringWithShadow("Admins: " + String.valueOf(this.admins.size()), ((new ScaledResolution(mc)).getScaledWidth() / 2 - mc.fontRenderer.getStringWidth("Admins: " + String.valueOf(this.admins.size())) + 20), ((new ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
            }
        }
        if (e instanceof EventUpdate) {
            if (this.timer.hasReached(5000.0F)) {
                this.timer.reset();
                mc.player.connection.sendPacket((Packet)new CPacketTabComplete("/vanishnopacket:vanish ", BlockPos.ORIGIN, false));
                mc.player.connection.sendPacket((Packet)new CPacketTabComplete("/rank ", BlockPos.ORIGIN, false));
            }
            if (this.admins.size() > 0)
                mc.ingameGUI.displayTitle("INC!!", "" + String.valueOf(this.admins.size()), 500, 2000, 500);
            setTag(String.valueOf(this.admins.size()));
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
                            mc.ingameGUI.displayTitle("INC!!", "" + String.valueOf(this.admins.size()), 500, 2000, 500);
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
