package org.infernalstudios.archeryexp.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Unique
    private Player getPlayer() {
        return (Player) (Object) this;
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void applyQuickshot(Entity target, CallbackInfo ci) {
        if (isCritting(target)) {
            LivingEntity living = (LivingEntity) target;

            living.getHandSlots().forEach(stack -> {
                if (stack.getItem() instanceof BowItem bow && living.getRandom().nextInt(100) < 33) {

                    int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);

                    float breakResistance = (level * 0.1f) + ((BowProperties) bow).getBreakingResistance();

                    int damage = getDurabilityLeft(stack) - Math.round(getDurabilityLeft(stack) * breakResistance);

                    stack.hurtAndBreak(damage, living, (entity) -> {
                        entity.broadcastBreakEvent(entity.getUsedItemHand());
                        entity.setItemInHand(entity.getUsedItemHand(), new ItemStack(Items.AIR)); // There's Prob a better way to refresh mob ai, but eh
                    });
                }
            });
        }
    }


    // This was ripped from the player attack method, I don't feel like translating the variables rn
    @Unique
    private boolean isCritting(Entity target) {
        // Borrowing this from player
        float $$4 = getPlayer().getAttackStrengthScale(0.5F);
        boolean $$5 = $$4 > 0.9F;
        boolean $$8 = $$5
                && getPlayer().fallDistance > 0.0F
                && !getPlayer().onGround()
                && !getPlayer().onClimbable()
                && !getPlayer().isInWater()
                && !getPlayer().hasEffect(MobEffects.BLINDNESS)
                && !getPlayer().isPassenger()
                && target instanceof LivingEntity;
        $$8 = $$8 && !getPlayer().isSprinting();

        return $$8;
    }

    @Unique
    public int getDurabilityLeft(ItemStack item) {
        int maxDurability = item.getMaxDamage();
        int currentDamage = item.getTag() == null ? 0 : item.getTag().getInt("Damage");
        return maxDurability - currentDamage;
    }

}
