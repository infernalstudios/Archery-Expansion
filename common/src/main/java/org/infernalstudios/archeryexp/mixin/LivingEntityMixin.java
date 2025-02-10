package org.infernalstudios.archeryexp.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.infernalstudios.archeryexp.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private LivingEntity getLivingEntity() {
        return (LivingEntity) (Object) this;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickStuff(CallbackInfo ci) {

        if (getLivingEntity().isUsingItem() && getLivingEntity().getUseItem().getItem() instanceof BowItem) {
            getLivingEntity().getArmorSlots().forEach(stack -> {
                int level = EnchantmentHelper.getEnchantmentLevel(ArcheryEnchants.GRIT, getLivingEntity());
                if (level > 0) {
                    getLivingEntity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, level - 1, false, false));
                }
            });
        }

        getLivingEntity().getArmorSlots().forEach(stack -> {
            int level = EnchantmentHelper.getEnchantmentLevel(ArcheryEnchants.PINCUSHIONING, getLivingEntity());
            if (level > 0) {
                getLivingEntity().addEffect(new MobEffectInstance(ArcheryEffects.QUICKDRAW_EFFECT, 30, level - 1, false, false));
            }

            level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.SCOUTING, stack);

            if (level > 0 && getLivingEntity().isUsingItem() && getLivingEntity().getUseItem().getItem() instanceof BowItem) {
                getLivingEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, level - 1, false, false));
            }
        });
    }

}
