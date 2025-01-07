package org.infernalstudios.archeryexp.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

    @Unique
    private AbstractClientPlayer getPlayer() {
        return (AbstractClientPlayer) (Object) this;
    }

    @Inject(
            method = "getFieldOfViewModifier",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true)
    private void bowSlowdown(CallbackInfoReturnable<Float> cir, float fov) {
        ItemStack item = getPlayer().getUseItem();
        if (getPlayer().isUsingItem()) {
            if (item.getItem() instanceof BowItem) {

                BowProperties bow = (BowProperties) item.getItem();

                AttributeInstance speedAttribute = getPlayer().getAttribute(Attributes.MOVEMENT_SPEED);

                if (speedAttribute != null) {
                    AttributeModifier drawModifier = speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID);

                    if (drawModifier != null) {
                        float movementSpeed = (float)getPlayer().getAttributeValue(Attributes.MOVEMENT_SPEED);
                        float walkingSpeed = getPlayer().getAbilities().getWalkingSpeed();
                        float multipler = bow.getMovementSpeedMultiplier();

                        fov /= (movementSpeed / walkingSpeed + 1.0f) / 2.0f;
                        fov *= ((movementSpeed / multipler) / walkingSpeed + 1.0f) / 2.0f;
                    }
                }

                int $$2 = getPlayer().getTicksUsingItem();
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
