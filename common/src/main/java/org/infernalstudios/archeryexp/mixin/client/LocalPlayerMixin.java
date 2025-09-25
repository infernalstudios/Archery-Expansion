package org.infernalstudios.archeryexp.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Shadow protected abstract boolean isControlledCamera();

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void archeryexp$serverAiStep(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        Item item = player.getUseItem().getItem();

        if (player.isUsingItem() && !player.isPassenger() && this.isControlledCamera() && item instanceof IBowProperties bow) {
            if (bow.archeryexp$isSpecial()) {
                player.xxa /= 0.2f; // side
                player.zza /= 0.2f; // front/back
            }
        }
    }
}
