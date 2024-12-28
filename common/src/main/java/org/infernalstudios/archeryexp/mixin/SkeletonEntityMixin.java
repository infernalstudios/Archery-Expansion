package org.infernalstudios.archeryexp.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.ShatteringArrowData;
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
public class SkeletonEntityMixin {

    @Shadow @Final private RangedBowAttackGoal<AbstractSkeleton> bowGoal;

    @Shadow @Final private MeleeAttackGoal meleeGoal;

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
            ((ShatteringArrowData) arrow).setShatterLevel(level);

            bow.getEffects().forEach(potionData -> {
                getSkeleton().addEffect(new MobEffectInstance(potionData.getEffect(), potionData.getLength(), potionData.getLevel(), true, true));
            });
        }
    }

    @Redirect(
            method = "reassessWeaponGoal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean modifyWeaponHand(ItemStack instance, Item item) {
        ItemStack handItem = getSkeleton().getMainHandItem();
        return handItem.getItem() instanceof BowItem;
    }
}
