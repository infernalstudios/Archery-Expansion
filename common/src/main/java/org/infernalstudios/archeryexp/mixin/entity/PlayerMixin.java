package org.infernalstudios.archeryexp.mixin.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.common.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.client.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.common.misc.ArcheryTags;
import org.infernalstudios.archeryexp.platform.Services;
import org.infernalstudios.archeryexp.util.ArcheryEnchantUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void archeryexp$tick(CallbackInfo ci) {
        Player user = (Player) (Object) this;
        ItemStack bowStack = user.getUseItem();

        AttributeInstance speedAttribute = user.getAttribute(Attributes.MOVEMENT_SPEED);

        if (user.isUsingItem() && bowStack.getItem() instanceof IBowProperties bow) {

            if (speedAttribute != null && bow.archeryexp$isSpecial()) {
                if (speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID) == null) {
                    AttributeModifier speedModifier = new AttributeModifier(
                            ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID,
                            "Bow Speed Boost",
                            bow.archeryexp$getWalkSpeed() - 1,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    );
                    speedAttribute.addTransientModifier(speedModifier);
                }
            }
        } else if (speedAttribute != null) {
            if (speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID) != null) {
                speedAttribute.removeModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID);
            }
        }

        if (!user.level().isClientSide()) {
            if (!ArcheryExpansion.BOW_STAT_PLAYER_LIST.contains((ServerPlayer) user)) {
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item instanceof BowItem bowItem) {
                        IBowProperties bow = (IBowProperties) bowItem;
                        if (bow.archeryexp$isSpecial()) {
                            Services.PLATFORM.sendBowStatsPacket(
                                    (ServerPlayer) user,
                                    bowItem.getDefaultInstance(),
                                    bow.archeryexp$getRange(),
                                    bow.archeryexp$getChargeTime(),
                                    bow.archeryexp$getWalkSpeed(),
                                    bow.archeryexp$getOffsetX(),
                                    bow.archeryexp$getOffsetY()
                            );
                        }
                    }
                }
                ArcheryExpansion.BOW_STAT_PLAYER_LIST.add((ServerPlayer) user);
            }
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void archeryexp$attack(Entity target, CallbackInfo ci) {
        Player user = (Player) (Object) this;

        if (archeryexp$isCritting(target) && user.getMainHandItem().is(ArcheryTags.CAN_BREAK_BOW) && target instanceof LivingEntity living) {
            living.getHandSlots().forEach(stack -> {

                if (stack.getItem() instanceof IBowProperties bow && living.getRandom().nextInt(100) < bow.archeryexp$getBreakChance() * 100) {
                    int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
                    float breakResistance = (level * 0.1f) + bow.archeryexp$getBreakResist();
                    int damage = archeryexp$getDurabilityLeft(stack) - Math.round(archeryexp$getDurabilityLeft(stack) * breakResistance);

                    stack.hurtAndBreak(damage, living, (entity) -> {
                        entity.broadcastBreakEvent(entity.getUsedItemHand());
                        entity.setItemInHand(entity.getUsedItemHand(), new ItemStack(Items.AIR)); // There's Prob a better way to refresh mob ai, but eh
                    });
                }
            });
        }
    }

    // This was ripped from the player attack method, I don't feel like translating the variables rn
    //
    // Future Deadly here, I'm still don't feel like doing that rn
    @Unique
    private boolean archeryexp$isCritting(Entity target) {
        Player user = (Player) (Object) this;

        // Borrowing this from player
        float $$4 = user.getAttackStrengthScale(0.5F);
        boolean $$5 = $$4 > 0.9F;
        boolean $$8 = $$5
                && user.fallDistance > 0.0F
                && !user.onGround()
                && !user.onClimbable()
                && !user.isInWater()
                && !user.hasEffect(MobEffects.BLINDNESS)
                && !user.isPassenger()
                && target instanceof LivingEntity;
        $$8 = $$8 && !user.isSprinting();

        return $$8;
    }

    @Unique
    public int archeryexp$getDurabilityLeft(ItemStack item) {
        return item.getMaxDamage() - (item.getTag() == null ? 0 : item.getTag().getInt("Damage"));
    }
}
