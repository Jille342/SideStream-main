package client.features.module.combat;

import client.features.module.Module;
import client.setting.NumberSetting;

public class Hitbox extends Module {
    public Hitbox() {
        super("Hitbox", 0,Category.COMBAT);
    }
public  static NumberSetting size;
    @Override
    public void init(){
        super.init();
        size = new NumberSetting("Hitbox", 0.08 , 0, 1,0.01F);
        addSetting(size);
    }
}
