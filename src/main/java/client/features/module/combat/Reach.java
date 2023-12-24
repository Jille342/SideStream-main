package client.features.module.combat;

import client.features.module.Module;
import client.setting.NumberSetting;

public class Reach  extends Module {

  public static NumberSetting reach;
    public Reach() {
        super("Reach", 0, Category.COMBAT);


    }


    @Override
    public void init(){
        super.init();
        reach = new NumberSetting("Reach", 3.0 , 3.0, 4,0.1F);
        addSetting(reach);
    }
}
