package org.infernalstudios.archeryexp.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

    @Inject(method = "getFieldOfViewModifier", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void archeryexp$getFieldOfViewModifier(CallbackInfoReturnable<Float> cir, float fov) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        ItemStack item = player.getUseItem();
        if (player.isUsingItem() && item.getItem() instanceof IBowProperties bow) {
            AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

            if (speedAttribute != null) {
                AttributeModifier drawModifier = speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID);

                if (drawModifier != null) {
                    float movementSpeed = (float) player.getAttributeValue(Attributes.MOVEMENT_SPEED);
                    float walkingSpeed = player.getAbilities().getWalkingSpeed();
                    float multipler = bow.archeryexp$getWalkSpeed();

                    fov /= (movementSpeed / walkingSpeed + 1.0f) / 2.0f;
                    fov *= ((movementSpeed / multipler) / walkingSpeed + 1.0f) / 2.0f;
                }
            }

            // Since the Vanilla bow still does zoom by default, exclude it here
            if (!item.is(Items.BOW)) {
                int i = player.getTicksUsingItem();
                float g = (float)i / 20.0F;
                if (g > 1.0F) {
                    g = 1.0F;
                } else {
                    g *= g;
                }

                fov *= 1.0F - g * 0.15F;
            }

            cir.setReturnValue(Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get().floatValue(), 1.0f, fov));
        }
    }
}
