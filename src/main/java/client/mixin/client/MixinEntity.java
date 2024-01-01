package client.mixin.client;

import client.features.module.Module;
import client.features.module.ModuleManager;
import client.Client;
import client.event.EventDirection;
import client.event.listeners.EventFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    protected Minecraft mc = Minecraft.getMinecraft();

    @Shadow @Final static DataParameter<Byte> FLAGS;
    @Shadow EntityDataManager dataManager;
    @Shadow int entityId;

    @Inject(method = {"setFlag"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void setFlag(int flag, boolean set, CallbackInfo callbackInfo) {
        if (mc == null)
            return;
        if (mc.player == null)
            return;

        if (mc.player.getEntityId() == entityId) {
            EventFlag e = new EventFlag(flag, set);
            e.setDirection(EventDirection.INCOMING);
            Client.onEvent(e);

            if (e.isCancelled()) {
                callbackInfo.cancel();
                byte b0 = ((Byte)this.dataManager.get(FLAGS)).byteValue();

                if (set)
                {
                    this.dataManager.set(FLAGS, Byte.valueOf((byte)(b0 | 1 << flag)));
                }
                else
                {
                    this.dataManager.set(FLAGS, Byte.valueOf((byte)(b0 & ~(1 << flag))));
                }
            }
        }
    }

    @Inject(method = {"getFlag"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void getFlag(int flag, CallbackInfoReturnable<Boolean> cir) {
        if (mc == null)
            return;
        if (mc.player == null)
            return;

        if (mc.player.getEntityId() == entityId) {
            EventFlag e = new EventFlag(flag, (((Byte)this.dataManager.get(FLAGS)).byteValue() & 1 << flag) != 0);
            e.setDirection(EventDirection.OUTGOING);
            Client.onEvent(e);

            cir.setReturnValue(e.set);
        }
    }


}
