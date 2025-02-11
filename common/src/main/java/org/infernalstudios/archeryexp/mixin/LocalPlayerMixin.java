package org.infernalstudios.archeryexp.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.BowItem;
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

    // The weird name with the mod ID prefixed is to avoid collisions with other mods that have mixins which add methods of the same name.
    // This method was previously named "getPlayer".
    @Unique
    private LocalPlayer archeryexp$self() {
        return (LocalPlayer) (Object) this;
    }

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void bowSlowdown(CallbackInfo ci) {

        ItemStack bowStack = archeryexp$self().getUseItem();

        if (archeryexp$self().isUsingItem() && !archeryexp$self().isPassenger() && this.isControlledCamera()
                && bowStack.getItem() instanceof BowItem) {

            BowProperties bow = (BowProperties) bowStack.getItem();

            if (bow.hasSpecialProperties()) {
                archeryexp$self().xxa /= 0.2f; // side
                archeryexp$self().zza /= 0.2f; // front/back
            }
        }
    }
}
