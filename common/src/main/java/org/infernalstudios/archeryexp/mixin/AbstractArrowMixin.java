package org.infernalstudios.archeryexp.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import org.infernalstudios.archeryexp.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.util.ShatteringArrowData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements ShatteringArrowData {

    @Unique
    private int shatteringLvl;

    @Unique
    private AbstractArrow getArrow() {
        return (AbstractArrow) (Object) this;
    }

    @Redirect(
            method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    private boolean modifyBaseDamage(Entity entity, DamageSource damageSource, float originalDamage) {
        MobEffect effect = ArcheryEffects.QUICKDRAW_EFFECT;
        if (entity instanceof LivingEntity living && living.hasEffect(effect)) {
            int hurt = (living.getEffect(effect).getAmplifier() + 1) * 2;
            return entity.hurt(damageSource, originalDamage + hurt);
        }

        return entity.hurt(damageSource, originalDamage);
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void enchantmentEffects(EntityHitResult hitResult, CallbackInfo ci) {
        if (hitResult.getEntity() instanceof LivingEntity target) {

            target.getArmorSlots().forEach(stack -> {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.FRAGILITY, stack);
                if (level > 0 && target.canBeHitByProjectile()) {
                    stack.hurtAndBreak(2 * level, target, (ignore) -> {});
                }

                if (getShatterLevel() > 0 && target.getRandom().nextInt(100) < 5 && target.canBeHitByProjectile()) {
                    int damage = getDurabilityLeft(stack) - Math.round(getDurabilityLeft(stack) * 0.05f);
                    stack.hurtAndBreak(damage, target, (ignore) -> {});
                }
            });

            Entity arrowOwner = getArrow().getOwner();

            if (arrowOwner instanceof LivingEntity user) {
                user.getArmorSlots().forEach(stack -> {
                    int level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.BABY_FACE, stack);
                    if (level > 0 && target.canBeHitByProjectile()) {
                        user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * level, level, false, false));
                    }

                    level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.FOLLOW_THROUGH, stack);
                    if (level > 0 && target.canBeHitByProjectile()) {
                        user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 25 * level, level - 1, false, false));
                    }
                });
            }
        }
    }

    @Override
    public int getShatterLevel() {
        return this.shatteringLvl;
    }

    @Override
    public void setShatterLevel(int lvl) {
        this.shatteringLvl = lvl;
    }

    @Unique
    public int getDurabilityLeft(ItemStack item) {
        int maxDurability = item.getMaxDamage();
        int currentDamage = item.getTag() == null ? 0 : item.getTag().getInt("Damage");
        return maxDurability - currentDamage;
    }
}
