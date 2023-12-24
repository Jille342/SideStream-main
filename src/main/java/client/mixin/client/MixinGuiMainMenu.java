package client.mixin.client;

import client.utils.RenderingUtils;
import client.Client;
import client.ui.gui.login.GuiAltLogin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;

import static net.minecraft.client.renderer.GlStateManager.disableFog;
import static net.minecraft.client.renderer.GlStateManager.disableLighting;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {
    @Unique

    @Inject(method = "initGui", at = @At("RETURN"))
    private void GuiMainMenu(CallbackInfo callbackInfo) {
        this.buttonList.add(new GuiButton(500, this.width / 2 + 104, this.height / 4 + 90 + 24 * 2, 98, 20, "login"));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 500)
        {
            this.mc.displayGuiScreen(new GuiAltLogin(this));
        }
    }


    /**
     * @author Jill
     * @reason test
     */
    @Overwrite
    private void drawPanorama(int p_drawPanorama_1_, int p_drawPanorama_2_, float p_drawPanorama_3_) throws FileNotFoundException {

    }
    /**
     * @author aa
     * @reason aa
     */
    @Overwrite
    private void renderSkybox(int p_renderSkybox_1_, int p_renderSkybox_2_, float p_renderSkybox_3_) {

    }
    @Inject(method = "drawScreen",at = @At( "HEAD"))
    private void drawScreen(CallbackInfo ci) {
        disableLighting();
        disableFog();
        RenderingUtils.drawImg(Client.background, 0, 0, width, height);
    }
}
