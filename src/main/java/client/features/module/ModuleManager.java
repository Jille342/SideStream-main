package client.features.module;

import client.features.module.combat.*;
import client.features.module.misc.*;
import client.features.module.movement.Anchor;
import client.features.module.movement.Sprint;
import client.features.module.movement.Step;
import client.features.module.movement.Yaw;
import client.features.module.player.*;
import client.features.module.render.*;
import client.features.module.render.Map;
import client.features.module.world.AutoBuild;
import client.features.module.world.AutoSlime;
import client.setting.BooleanSetting;
import client.setting.KeyBindSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.Client;
import client.event.Event;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {

	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();

	public static void registerModules(){
		modules.add(new ClickGUI());
		modules.add(new Step());
		modules.add(new Yaw());
		modules.add(new Fullbright());
		modules.add(new InvMove());
		modules.add(new Tracers());
		modules.add(new AntiHunger());
		modules.add(new FreeCam());
		modules.add(new StorageESP());
		modules.add(new AutoSlime());
		modules.add(new AutoBuild());
		modules.add(new PacketCanceller());
		modules.add(new Map());
		modules.add(new ESP());
		modules.add(new AntiChunkBan());
		modules.add(new Debug());
        modules.add(new HUD());
		modules.add(new FreeLook());
		modules.add(new Notification());
		modules.add(new HoleESP());
		modules.add(new FakePlayer());;
		modules.add(new Burrow());
		modules.add(new Anchor());
		modules.add(new ForcePlace());
		modules.add(new AutoClicker());
		modules.add(new Sprint());
		modules.add(new AimAssist());
		modules.add(new BetterFightSound());
		modules.add(new HUD2());
		modules.add(new BowAimbot());
		modules.add(new NameTags());
		modules.add(new NameProtect());
		modules.add(new AntiForgeBypass());
		modules.add(new WTap());
		modules.add(new AutoTool());
		modules.add(new AutoSword());
		modules.add(new ESP2());
		modules.add(new ToggleSneak());
		modules.add(new HitBox());
		modules.add(new AdminChecker());
		modules.add(new FastBreak());
		modules.add(new SuccubusChecker());
		modules.add(new HitDelayFix());
		modules.add(new Reach2());
	}

	public static class ModuleComparator implements Comparator<Module> {
		@Override
		public int compare(Module o1, Module o2) {
			if(o1.priority > o2.priority)
				return -1;
			if(o1.priority < o2.priority)
				return 1;
			return 0;
		}
	}

	public static void onEvent(Event<?> e) {
		Collections.sort(ModuleManager.modules, new ModuleComparator());

		ModuleManager.modules.stream().forEach(m -> {
			if(m.isEnable()) m.onEvent(e);
		});
	}

	public static List<Module> getModulesbyCategory(Module.Category c) {
		List<Module> moduleList = new ArrayList<>();
		for(Module m : modules)
			if(m.getCategory() == c)
				moduleList.add(m);
		return moduleList;
	}

	public static Module getModulebyClass(Class<? extends Module> c) {
		return modules.stream().filter(m -> m.getClass() == c).findFirst().orElse(null);
	}

	public static Module getModulebyName(String str) {
		return modules.stream().filter(m -> m.getName() == str).findFirst().orElse(null);
	}

	public static void toggle(Class<? extends Module> c) {
		Module module = modules.stream().filter(m -> m.getClass() == c).findFirst().orElse(null);
		if(module != null)
			module.toggle();
	}

	public static void saveModuleSetting() {
		File directory = new File(Minecraft.getMinecraft().gameDir, Client.NAME);
		File setting = new File(directory, "Settings");

		if(!directory.exists()){
			directory.mkdir();
		}
		if(!setting.exists()){
			setting.mkdir();
		}

		try{
			for (Module m : modules){
				File module = new File(setting, m.getName());
				if (!module.exists()) {
					module.createNewFile();
				}

				PrintWriter pw = new PrintWriter(module);

				final String[] str = {""};

				str[0] += m.isEnable()?"1":"0";
				str[0] += "\n";

				m.settings.forEach(s -> {
					if(s instanceof KeyBindSetting){
						str[0] += "0"+String.valueOf(((KeyBindSetting) s).getKeyCode());
					}
					if(s instanceof BooleanSetting){
						str[0] += ((BooleanSetting)s).isEnable()?"11":"10";
					}
					if(s instanceof ModeSetting){
						str[0] += "2"+ ((ModeSetting) s).index;
					}
					if(s instanceof NumberSetting){
						str[0] += "3"+ String.valueOf(((NumberSetting) s).value);
					}
					str[0] += "\n";
				});

				pw.print(str[0]);
				pw.close();
			}
		}catch (IOException e){

		}
	}

	public static void loadModuleSetting() {
		File directory = new File(Minecraft.getMinecraft().gameDir, Client.NAME);
		File setting = new File(directory, "Settings");

		if (setting.isDirectory()){
			for (Module m : modules) {
				File SettingFile = new File(setting, m.getName());
				try {
					FileReader filereader = new FileReader(SettingFile);
					int ch;
					String str = "";
					while((ch = filereader.read()) != -1){
						str += String.valueOf((char)ch);
					}
					int i = 0;
					for (String val : Arrays.asList(str.split("\n"))) {
						if(i == 0) {
							m.setEnable(val.equals("1")?true:false);
						}else {

							String dat = val.substring(1);
							if (val.startsWith("0")) {
								KeyBindSetting bind = (KeyBindSetting)m.settings.get(i-1);
								bind.keyCode = Integer.parseInt(dat);
							}
							if (val.startsWith("1")) {
								BooleanSetting bind = (BooleanSetting)m.settings.get(i-1);
								if(val.equals("11"))
								bind.setEnable(true);
							}
							if (val.startsWith("2")) {
								ModeSetting bind = (ModeSetting)m.settings.get(i-1);
								bind.index = Integer.parseInt(dat);
							}
							if (val.startsWith("3")) {
								NumberSetting bind = (NumberSetting)m.settings.get(i-1);
								bind.value = Double.parseDouble(dat);
							}
						}
						i++;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException | ClassCastException | StringIndexOutOfBoundsException e) {
					e.printStackTrace();
					SettingFile.delete();
				}
			}
		}
	}


}
