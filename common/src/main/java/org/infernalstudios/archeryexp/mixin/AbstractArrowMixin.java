package org.infernalstudios.archeryexp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.EntityHitResult;
import org.infernalstudios.archeryexp.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.util.ArrowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements ArrowProperties {

    @Unique
    private int shatteringLvl;
    @Unique
    private int headshotLvl;

    @Unique
    private AbstractArrow getArrow() {
        return (AbstractArrow) (Object) this;
    }

    @WrapOperation(
            method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    private boolean modifyBaseDamage(Entity entity, DamageSource damageSource, float originalDamage, Operation<Boolean> original) {
        MobEffect effect = ArcheryEffects.QUICKDRAW_EFFECT;
        float hurtAmount = 0;

        if (entity instanceof LivingEntity living) {
            boolean playSound = false;
            if (living.hasEffect(effect))  {
                int hurt = (living.getEffect(effect).getAmplifier() + 1) * 2;
                playSound = true;
                hurtAmount += hurt;
            }

            double headPosition = living.position().add(0.0, living.getDimensions(living.getPose()).height * 0.85, 0.0).y - 0.17;

            if (getHeadshotLevel() > 0 && living.canBeHitByProjectile() && getArrow().position().y > headPosition && !inHeadshotBlacklist(living)) {
                hurtAmount += getHeadshotLevel() * 1.5f;
                playSound = true;
                if (living.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ArcheryParticles.HEADSHOT,
                            living.getX(), living.getEyeY() + 0.5, living.getZ(),
                            1,
                            0.0,
                            0.0,
                            0.0,
                            0.0
                    );
                }
            }

            if (playSound && getArrow().getOwner() instanceof Player player) {
                entity.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_HIT_PLAYER, entity.getSoundSource(), 1, 1);
            }
        }

        return original.call(entity, damageSource, originalDamage + hurtAmount);
    }

    @Unique
    private boolean inHeadshotBlacklist(LivingEntity entity) {
        return entity instanceof Animal || entity instanceof WaterAnimal || entity instanceof Slime || entity instanceof EnderDragon;
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void enchantmentEffects(EntityHitResult hitResult, CallbackInfo ci) {
        if (hitResult.getEntity() instanceof LivingEntity target) {

            target.getArmorSlots().forEach(stack -> {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.FRAGILITY, stack);
                if (level > 0 && target.canBeHitByProjectile()) {
                    stack.hurtAndBreak(2 * level, target, (ignore) -> {});
                }

                if (getShatterLevel() > 0 && target.getRandom().nextInt(100) < 5 * ((getShatterLevel() * 0.5) + 0.5) && target.canBeHitByProjectile()) {
                    int damage = Math.round(getDurabilityLeft(stack) * 0.05f);
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

    @Override
    public void setHeadshotLevel(int lvl) {
        this.headshotLvl = lvl;
    }

    @Override
    public int getHeadshotLevel() {
        return this.headshotLvl;
    }

    @Unique
    public int getDurabilityLeft(ItemStack item) {
        int maxDurability = item.getMaxDamage();
        int currentDamage = item.getTag() == null ? 0 : item.getTag().getInt("Damage");
        return maxDurability - currentDamage;
    }
}
