package org.infernalstudios.archeryexp.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;
import org.infernalstudios.archeryexp.util.PlayerFOV;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerFOV {

    private double lastFOV;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(Level $$0, BlockPos $$1, float $$2, GameProfile $$3, CallbackInfo ci) {
        this.lastFOV = 0;
    }

    @Unique
    private Player getPlayer() {
        return (Player) (Object) this;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void playerTick(CallbackInfo ci) {

        Player user = getPlayer();
        ItemStack bowStack = user.getUseItem();

        AttributeInstance speedAttribute = getPlayer().getAttribute(Attributes.MOVEMENT_SPEED);

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
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void applyQuickshot(Entity target, CallbackInfo ci) {
        if (isCritting(target) && getPlayer().getMainHandItem().is(ItemTags.AXES)) {
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

    @Override
    public double getPlayerFOVWithoutBow() {
        return this.lastFOV;
    }

    @Override
    public void setPlayerFOVWithoutBow(double fov) {
        this.lastFOV = fov;
    }
}
