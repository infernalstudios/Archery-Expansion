package org.infernalstudios.archeryexp.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

    // The weird name with the mod ID prefixed is to avoid collisions with other mods that have mixins which add methods of the same name.
    // This method was previously named "getPlayer".
    @Unique
    private AbstractClientPlayer archeryexp$self() {
        return (AbstractClientPlayer) (Object) this;
    }

    @Inject(
            method = "getFieldOfViewModifier",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true)
    private void bowSlowdown(CallbackInfoReturnable<Float> cir, float fov) {
        ItemStack item = archeryexp$self().getUseItem();
        if (archeryexp$self().isUsingItem()) {
            if (item.getItem() instanceof BowItem) {

                BowProperties bow = (BowProperties) item.getItem();

                AttributeInstance speedAttribute = archeryexp$self().getAttribute(Attributes.MOVEMENT_SPEED);

                if (speedAttribute != null) {
                    AttributeModifier drawModifier = speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID);

                    if (drawModifier != null) {
                        float movementSpeed = (float) archeryexp$self().getAttributeValue(Attributes.MOVEMENT_SPEED);
                        float walkingSpeed = archeryexp$self().getAbilities().getWalkingSpeed();
                        float multipler = bow.getMovementSpeedMultiplier();

                        fov /= (movementSpeed / walkingSpeed + 1.0f) / 2.0f;
                        fov *= ((movementSpeed / multipler) / walkingSpeed + 1.0f) / 2.0f;
                    }
                }

                int $$2 = archeryexp$self().getTicksUsingItem();
                float $$3 = (float)$$2 / 20.0F;
                if ($$3 > 1.0F) {
                    $$3 = 1.0F;
                } else {
                    $$3 *= $$3;
                }

                fov *= 1.0f - $$3 * 0.15f;
                cir.setReturnValue(Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get().floatValue(), 1.0f, fov));
            }
        }
    }
}
