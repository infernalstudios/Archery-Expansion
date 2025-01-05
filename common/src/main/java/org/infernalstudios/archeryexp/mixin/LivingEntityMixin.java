package org.infernalstudios.archeryexp.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.PlayerFOV;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private LivingEntity getLivingEntity() {
        return (LivingEntity) (Object) this;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickStuff(CallbackInfo ci) {
        LivingEntity entity = getLivingEntity();

        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (entity.isUsingItem() && entity.getUseItem().getItem() instanceof BowItem bow) {

            if (speedAttribute != null) {
                if (speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID) == null) {

                    AttributeModifier speedModifier = new AttributeModifier(
                            ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID,
                            "Bow Speed Boost",
                            1.7 - ((BowProperties) bow).getMovementSpeedMultiplier(),
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    );
                    speedAttribute.addTransientModifier(speedModifier);
                }
            }

            entity.getArmorSlots().forEach(stack -> {
                int level = EnchantmentHelper.getEnchantmentLevel(ArcheryEnchants.GRIT, entity);
                if (level > 0) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, level - 1, false, false));
                }
            });

        }
        else {
            if (speedAttribute != null) {
                if (speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID) != null) {
                    speedAttribute.removeModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID);
                }
            }
        }

        entity.getArmorSlots().forEach(stack -> {
            int level = EnchantmentHelper.getEnchantmentLevel(ArcheryEnchants.PINCUSHIONING, entity);
            if (level > 0) {
                entity.addEffect(new MobEffectInstance(ArcheryEffects.QUICKDRAW_EFFECT, 30, level - 1, false, false));
            }

            level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.SCOUTING, stack);

            if (level > 0 && entity.isUsingItem() && entity.getUseItem().getItem() instanceof BowItem) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, level - 1, false, false));
            }
        });
    }

}
