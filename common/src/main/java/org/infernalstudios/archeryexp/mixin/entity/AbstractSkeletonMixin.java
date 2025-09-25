package org.infernalstudios.archeryexp.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import org.infernalstudios.archeryexp.util.ArcheryEnchantUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.infernalstudios.archeryexp.util.mixinterfaces.IArrowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin {

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity archeryexp$performRangedAttack(Entity par1, @Local(argsOnly = true, ordinal = 0) LivingEntity $$0) {
        ItemStack stack = $$0.getMainHandItem();

        if (par1 instanceof AbstractArrow arrow && stack.getItem() instanceof IBowProperties bow && bow.archeryexp$isSpecial()) {
            arrow.setBaseDamage(bow.archeryexp$getBaseDamage());
            ((IArrowProperties) arrow).archeryexp$setShatterLevel(stack);
            ((IArrowProperties) arrow).archeryexp$setHeadshotLevel(stack);
        }
        return par1;
    }

    @Inject(method = "performRangedAttack", at = @At(value = "TAIL"))
    private void archeryexp$performRangedAttack(LivingEntity $$0, float $$1, CallbackInfo ci) {
        AbstractSkeleton skeleton = (AbstractSkeleton) (Object) this;
        ItemStack stack = skeleton.getMainHandItem();

        ArcheryEnchantUtil.enchantmentAction(() -> Enchantments.POWER_ARROWS, skeleton, stack, true,
                (lvl) -> stack.hurtAndBreak(lvl, skeleton, (ignore) -> {}));

        if (stack.getItem() instanceof IBowProperties bow && bow.archeryexp$isSpecial()) {
            bow.archeryexp$getEffects().forEach(effect -> effect.apply(skeleton));
            bow.archeryexp$getParticles().forEach(particle -> particle.apply(skeleton));
        }
    }

    @WrapOperation(method = "reassessWeaponGoal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean archeryexp$reassessWeaponGoal(ItemStack instance, Item $$0, Operation<Boolean> original) {
        AbstractSkeleton skeleton = (AbstractSkeleton) (Object) this;
        return original.call(instance, $$0) || skeleton.getMainHandItem().getItem() instanceof IBowProperties;
    }
}
