package client.mixin.client;

import client.features.module.ModuleManager;
import client.Client;
import client.event.listeners.EventTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    public GuiScreen currentScreen;


    @Inject(method = {"shutdown"}, at = @At("HEAD"))
    public void shutdown(CallbackInfo ci)
    {
        ModuleManager.saveModuleSetting();
    }
    @Inject(method ="runTick", at = @At("RETURN"))
    private void runTick(CallbackInfo ci) {
        EventTick eventTick = new EventTick();
        Client.onEvent(eventTick);
    }




}
