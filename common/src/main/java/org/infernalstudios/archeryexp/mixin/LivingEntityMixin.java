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

    // The weird name with the mod ID prefixed is to avoid collisions with other mods that have mixins which add methods of the same name.
    // This method was previously named "getLivingEntity".
    @Unique
    private LivingEntity archeryexp$self() {
        return (LivingEntity) (Object) this;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickStuff(CallbackInfo ci) {

        if (archeryexp$self().isUsingItem() && archeryexp$self().getUseItem().getItem() instanceof BowItem) {
            archeryexp$self().getArmorSlots().forEach(stack -> {
                int level = EnchantmentHelper.getEnchantmentLevel(ArcheryEnchants.GRIT, archeryexp$self());
                if (level > 0) {
                    archeryexp$self().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, level - 1, false, false));
                }
            });
        }

        archeryexp$self().getArmorSlots().forEach(stack -> {
            int level = EnchantmentHelper.getEnchantmentLevel(ArcheryEnchants.PINCUSHIONING, archeryexp$self());
            if (level > 0) {
                archeryexp$self().addEffect(new MobEffectInstance(ArcheryEffects.QUICKDRAW_EFFECT, 30, level - 1, false, false));
            }

            level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.SCOUTING, stack);

            if (level > 0 && archeryexp$self().isUsingItem() && archeryexp$self().getUseItem().getItem() instanceof BowItem) {
                archeryexp$self().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, level - 1, false, false));
            }
        });
    }

}
