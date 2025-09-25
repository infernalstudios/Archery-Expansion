package org.infernalstudios.archeryexp.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import org.infernalstudios.archeryexp.common.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.common.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.client.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.common.misc.ArcheryTags;
import org.infernalstudios.archeryexp.util.ArcheryEnchantUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IArrowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements IArrowProperties {

    @Unique private int archeryexp$shatteringLvl, archeryexp$headshotLvl;

    @WrapOperation(
            method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    private boolean archeryexp$onHitEntity(Entity entity, DamageSource damageSource, float originalDamage, Operation<Boolean> original) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;

        MobEffect effect = ArcheryEffects.QUICKDRAW_EFFECT.get();
        float hurtAmount = 0;

        if (entity instanceof LivingEntity living) {
            boolean playSound = false;
            if (living.hasEffect(effect))  {
                int hurt = (living.getEffect(effect).getAmplifier() + 1) * 2;
                playSound = true;
                hurtAmount += hurt;
            }

            double headPosition = living.position().add(0.0, living.getDimensions(living.getPose()).height * 0.85, 0.0).y - 0.17;

            if (this.archeryexp$headshotLvl > 0 && living.canBeHitByProjectile() && arrow.position().y > headPosition && living.getType().is(ArcheryTags.ALLOW_HEADSHOT)) {
                hurtAmount += this.archeryexp$headshotLvl * 2;
                playSound = true;
                if (living.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ArcheryParticles.HEADSHOT.get(), living.getX(), living.getEyeY() + 0.5, living.getZ(), 1, 0, 0, 0, 0);
                }
            }

            if (playSound && arrow.getOwner() instanceof Player player) {
                entity.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_HIT_PLAYER, entity.getSoundSource(), 1, 1);
            }
        }

        return original.call(entity, damageSource, originalDamage + hurtAmount);
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void archeryexp$onHitEntity(EntityHitResult hitResult, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;

        if (hitResult.getEntity() instanceof LivingEntity target) {
            boolean bl = target.canBeHitByProjectile();

            target.getArmorSlots().forEach(stack -> {
                ArcheryEnchantUtil.enchantmentAction(ArcheryEnchants.FRAGILITY, target, stack, bl, (lvl) -> stack.hurtAndBreak(2 * lvl, target, (ignore) -> {}));

                if (this.archeryexp$shatteringLvl > 0 && target.getRandom().nextInt(100) < 5 * ((this.archeryexp$shatteringLvl * 0.5) + 0.5) && target.canBeHitByProjectile()) {
                    int damage = Math.round(archeryexp$getDurabilityLeft(stack) * 0.05f);
                    stack.hurtAndBreak(damage, target, (ignore) -> {});
                }
            });

            if (arrow.getOwner() instanceof LivingEntity user) {
                user.getArmorSlots().forEach(stack -> {
                    ArcheryEnchantUtil.effectEnchantmentAction(ArcheryEnchants.BABY_FACE, user, stack, bl, MobEffects.MOVEMENT_SPEED, 20, true);
                    ArcheryEnchantUtil.effectEnchantmentAction(ArcheryEnchants.FOLLOW_THROUGH, user, stack, bl, MobEffects.DAMAGE_BOOST, 25, true);
                });
            }
        }
    }

    @Override
    public void archeryexp$setShatterLevel(ItemStack stack) {
        this.archeryexp$shatteringLvl = archeryexp$setEnchantModLvl(ArcheryEnchants.SHATTERING, stack);
    }
    @Override
    public void archeryexp$setHeadshotLevel(ItemStack stack) {
        this.archeryexp$headshotLvl = archeryexp$setEnchantModLvl(ArcheryEnchants.HEADSHOT, stack);
    }

    @Unique
    private int archeryexp$setEnchantModLvl(Supplier<Enchantment> enchant, ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchant.get(), stack);
    }

    @Unique
    public int archeryexp$getDurabilityLeft(ItemStack item) {
        return item.getMaxDamage() - (item.getTag() == null ? 0 : item.getTag().getInt("Damage"));
    }
}
