package client;

import client.command.CommandManager;
import client.event.Event;
import client.event.listeners.EventChat;
import client.event.listeners.EventPacket;
import client.features.module.ModuleManager;
import client.ui.HUD;
import client.ui.HUD2;
import client.ui.gui.clickGUI.GuiClickGUI;
import client.ui.theme.ThemeManager;
import client.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = Client.MOD_ID, name = Client.NAME, version = Client.VERSION)
public class Client
{
    public static final String MOD_ID = "sidestream";
    public static final String NAME = "SideStream";
    public static final String VERSION = "0.1 Beta";
	public static HUD2 hud2 = new HUD2();
	public static ResourceLocation background = new ResourceLocation("client/background.png");


	public static HUD hud = new HUD();
	public static ThemeManager themeManager = new ThemeManager();
	public static CommandManager commandManager = new CommandManager();
	public static Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		MinecraftForge.EVENT_BUS.register(this);
		commandManager.init();
		ModuleManager.registerModules();
		ModuleManager.loadModuleSetting();
		GuiClickGUI.loadModules();
	}

	public static Event<?> onEvent(Event<?> e) {
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			if (p instanceof SPacketTimeUpdate) {
				WorldUtils.onTime((SPacketTimeUpdate) p);
			}
		}
    	ModuleManager.onEvent(e);
		return e;
	}

	@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent e) {
		if (mc.currentScreen == null) {
			try {
				if (Keyboard.isCreated()) {
					if (Keyboard.getEventKeyState()) {
						int i = Keyboard.getEventKey();
						if (i != 0) {
							ModuleManager.modules.stream().forEach(m -> {
								if(m.getKeyCode() == i) m.toggle();
							});
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void chatEvent(ClientChatEvent event) {
		String message = event.getMessage();

		if (commandManager.handleCommand(message)) {
			event.setCanceled(true);
		}else {
			event.setMessage(message+"");
		}

		onEvent(new EventChat(message));
	}
}
