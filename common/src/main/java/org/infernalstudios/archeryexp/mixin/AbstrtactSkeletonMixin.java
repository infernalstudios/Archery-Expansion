package org.infernalstudios.archeryexp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.ArrowProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractSkeleton.class)
public abstract class AbstrtactSkeletonMixin {

    @Shadow @Final private RangedBowAttackGoal<AbstractSkeleton> bowGoal;

    @Shadow @Final private MeleeAttackGoal meleeGoal;

    @Shadow public abstract void setItemSlot(EquipmentSlot $$0, ItemStack $$1);

    @Unique
    private AbstractSkeleton getSkeleton() {
        return (AbstractSkeleton) (Object) this;
    }

    @Inject(method = "performRangedAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void rangedAttack(LivingEntity target, float $$1, CallbackInfo ci, ItemStack $$2, AbstractArrow arrow, double $$4, double $$5, double $$6, double $$7) {

        ItemStack stack = getSkeleton().getMainHandItem();

        int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
        if (level > 0) {
            stack.hurtAndBreak(level, getSkeleton(), (ignore) -> {});
        }

        BowProperties bow = (BowProperties) stack.getItem();

        if (bow.hasSpecialProperties()) {
            arrow.shootFromRotation(getSkeleton(), getSkeleton().getXRot(), getSkeleton().getYRot(), 0.0f, 1.6f, (float)(14 - getSkeleton().level().getDifficulty().getId() * 4));
            arrow.setBaseDamage(bow.getBaseDamage());

            level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.SHATTERING, stack);
            ((ArrowProperties) arrow).setShatterLevel(level);

            level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.HEADSHOT, stack);
            ((ArrowProperties) arrow).setHeadshotLevel(level);

            bow.getEffects().forEach(potionData -> {
                getSkeleton().addEffect(new MobEffectInstance(potionData.getEffect(), potionData.getLength(), potionData.getLevel(), true, true));
            });

            bow.getParticles().forEach(particleData -> {
                if (getSkeleton().level() instanceof ServerLevel serverLevel) {

                    Vec3 o = particleData.getPosOffset();
                    Vec3 v = particleData.getVelocity();

                    Vec3 lookVector = getSkeleton().getLookAngle();

                    Vec3 inFrontPos = getSkeleton().position().add(lookVector.scale(particleData.getLookOffset()));

                    serverLevel.sendParticles(
                            particleData.getType(),
                            inFrontPos.x + o.x, getSkeleton().getEyeY() + o.y, inFrontPos.z() + o.z,
                            particleData.getCount(),
                            v.x,
                            v.y,
                            v.z,
                            0.0
                    );
                }
            });
        }
    }

    @WrapOperation(
            method = "reassessWeaponGoal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean modifyWeaponHand(ItemStack instance, Item $$0, Operation<Boolean> original) {
        ItemStack handItem = getSkeleton().getMainHandItem();
        return original.call(instance, $$0) ||  handItem.getItem() instanceof BowItem;
    }
}
