package org.infernalstudios.archeryexp.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Shadow protected abstract boolean isControlledCamera();

    @Shadow protected int sprintTriggerTime;

    @Unique
    private LocalPlayer getPlayer() {
        return (LocalPlayer) (Object) this;
    }

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void bowSlowdown(CallbackInfo ci) {

        ItemStack bowStack = getPlayer().getUseItem();

        if (getPlayer().isUsingItem() && !getPlayer().isPassenger() && this.isControlledCamera()
                && bowStack.getItem() instanceof BowItem) {

            BowProperties bow = (BowProperties) bowStack.getItem();

            if (bow.hasSpecialProperties()) {
                getPlayer().xxa /= 0.2f; // side
                getPlayer().zza /= 0.2f; // front/back

                getPlayer().xxa *= bow.getMovementSpeedMultiplier(); // side
                getPlayer().zza *= bow.getMovementSpeedMultiplier(); // front/back
                this.sprintTriggerTime = 0;
            }
        }
    }
}
