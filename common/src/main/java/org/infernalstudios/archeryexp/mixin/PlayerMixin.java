package org.infernalstudios.archeryexp.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
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
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.platform.Services;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Unique
    private double lastFOV;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(Level $$0, BlockPos $$1, float $$2, GameProfile $$3, CallbackInfo ci) {
        this.lastFOV = 0;
    }

    // The weird name with the mod ID prefixed is to avoid collisions with other mods that have mixins which add methods of the same name.
    // This method was previously named "getPlayer".
    @Unique
    private Player archeryexp$player() {
        return (Player) (Object) this;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void playerTick(CallbackInfo ci) {

        Player user = archeryexp$player();
        ItemStack bowStack = user.getUseItem();

        AttributeInstance speedAttribute = user.getAttribute(Attributes.MOVEMENT_SPEED);

        if (user.isUsingItem() && bowStack.getItem() instanceof BowItem bow) {

            BowProperties bp = (BowProperties) bow;

            if (speedAttribute != null && bp.hasSpecialProperties()) {
                if (speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID) == null) {
                    AttributeModifier speedModifier = new AttributeModifier(
                            ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID,
                            "Bow Speed Boost",
                            ((BowProperties) bow).getMovementSpeedMultiplier() - 1,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    );
                    speedAttribute.addTransientModifier(speedModifier);
                }
            }

            int level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.TRAJECTORY, bowStack);

            if (level > 0) {
                Level world = user.level();
                List<Vec3> points = BowUtil.getBowTrajectoryPoints(user, bow);

                points.forEach(p -> {
                    world.addParticle(ArcheryParticles.ARROW_TRAIL, p.x, p.y, p.z, 0, 0, 0);
                });
            }
        }
        else if (speedAttribute != null) {
            if (speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID) != null) {
                speedAttribute.removeModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID);
            }
        }

        if (!archeryexp$player().level().isClientSide()) {
            if (!ArcheryExpansion.bowStatPlayerList.contains((ServerPlayer) user)) {
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item instanceof BowItem bowItem) {
                        BowProperties bow = (BowProperties) bowItem;
                        if (bow.hasSpecialProperties()) {
                            Services.PLATFORM.sendBowStatsPacket(
                                    (ServerPlayer) user,
                                    bowItem.getDefaultInstance(),
                                    bow.getRange(),
                                    bow.getChargeTime(),
                                    bow.getMovementSpeedMultiplier(),
                                    bow.getOffsetX(),
                                    bow.getOffsetY()
                            );
                        }
                    }
                }
                ArcheryExpansion.bowStatPlayerList.add((ServerPlayer) user);
            }
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void applyQuickshot(Entity target, CallbackInfo ci) {
        if (isCritting(target) && archeryexp$player().getMainHandItem().is(ItemTags.AXES)) {
            LivingEntity living = (LivingEntity) target;

            living.getHandSlots().forEach(stack -> {
                if (stack.getItem() instanceof BowItem bow && living.getRandom().nextInt(100) < (((BowProperties) bow).getBreakingChance() * 100)) {

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
        float $$4 = archeryexp$player().getAttackStrengthScale(0.5F);
        boolean $$5 = $$4 > 0.9F;
        boolean $$8 = $$5
                && archeryexp$player().fallDistance > 0.0F
                && !archeryexp$player().onGround()
                && !archeryexp$player().onClimbable()
                && !archeryexp$player().isInWater()
                && !archeryexp$player().hasEffect(MobEffects.BLINDNESS)
                && !archeryexp$player().isPassenger()
                && target instanceof LivingEntity;
        $$8 = $$8 && !archeryexp$player().isSprinting();

        return $$8;
    }

    @Unique
    public int getDurabilityLeft(ItemStack item) {
        int maxDurability = item.getMaxDamage();
        int currentDamage = item.getTag() == null ? 0 : item.getTag().getInt("Damage");
        return maxDurability - currentDamage;
    }
}
