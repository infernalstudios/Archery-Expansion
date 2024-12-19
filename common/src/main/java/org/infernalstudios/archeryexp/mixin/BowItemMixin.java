package org.infernalstudios.archeryexp.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.PotionData;
import org.infernalstudios.archeryexp.util.ShatteringArrowData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(BowItem.class)
public abstract class BowItemMixin implements BowProperties {

    @Shadow public abstract InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2);

    @Unique
    private int cooldown;
    @Unique
    private int drawTime;
    @Unique
    private float range;
    @Unique
    private float baseDamage;
    @Unique
    private float breakingResistance;
    @Unique
    private float movementSpeedMultiplier;
    @Unique
    private float recoil;
    @Unique
    private boolean hasSpecialProperties;

    @Unique
    private List<PotionData> effects = new ArrayList<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(Item.Properties properties, CallbackInfo ci) {
        this.cooldown = 0;
        this.baseDamage = 0;
        this.range = 0;
        this.drawTime = 0;
        this.breakingResistance = 0;
        this.movementSpeedMultiplier = 0;
        this.recoil = 0;

        this.hasSpecialProperties = false;
    }

    @Unique
    private Item getItem() {
        return (Item) (Object) this;
    }

    @Inject(
            method = "releaseUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void onReleaseUsing(ItemStack stack, Level world, LivingEntity entity, int remainingUseTicks, CallbackInfo ci,
                                Player user, boolean $$5, ItemStack $$6, int $$7, float $$8, boolean $$9, ArrowItem arrowItem, AbstractArrow arrow) {

        int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
        if (level > 0) {
            stack.hurtAndBreak(level, user, (ignore) -> {});
        }

        if (this.hasSpecialProperties) {
            float shoot = getPowerForDrawTime($$7);
            arrow.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0f, shoot * 3.0f + getRange(), 1.0f);
            arrow.setBaseDamage(getBaseDamage());
            arrow.setCritArrow(shoot == 1.0f);

            level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.SHATTERING, stack);
            ((ShatteringArrowData) arrow).setShatterLevel(level);
            user.getCooldowns().addCooldown(getItem(), getBowCooldown());

            this.effects.forEach(potionData -> {
                user.addEffect(new MobEffectInstance(potionData.getEffect(), potionData.getLength(), potionData.getLevel(), true, true));
            });

            applyRecoil(user, getRecoil());
        }
    }

    @Unique
    private void applyRecoil(Player user, double amount) {
        Vec3 lookDirection = user.getViewVector(1.0f);
        Vec3 knockbackVector = lookDirection.multiply(-amount, -amount, -amount);

        user.setDeltaMovement(
                user.getDeltaMovement().x + knockbackVector.x,
                user.getDeltaMovement().y + knockbackVector.y,
                user.getDeltaMovement().z + knockbackVector.z
        );
        user.hurtMarked = true;
    }

    public float getPowerForDrawTime(int drawTime) {
        float power = (float) drawTime / getChargeTime();
        power = (power * power + power * 2.0F) / 3.0F;

        if (power > 1.0F) {
            power = 1.0F;
        }

        return power;
    }

    @Override
    public int getBowCooldown() {
        return this.cooldown;
    }
    @Override
    public void setBowCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public float getBaseDamage() {
        return this.baseDamage;
    }

    @Override
    public void setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
    }

    @Override
    public float getRange() {
        return this.range;
    }

    @Override
    public void setRange(float range) {
        this.range = range;
    }

    public int getChargeTime() {
        return this.drawTime;
    }

    @Override
    public void setChargeTime(int chargeTime) {
        this.drawTime = chargeTime;
    }

    @Override
    public void setSpecialProperties(boolean hasProperties) {
        this.hasSpecialProperties = hasProperties;
    }

    @Override
    public void setBreakingResistance(float breakingResistance) {
        this.breakingResistance = breakingResistance;
    }

    @Override
    public float getBreakingResistance() {
        if (!this.hasSpecialProperties) {
            return 1.0f;
        }
        return this.breakingResistance;
    }

    @Override
    public float getMovementSpeedMultiplier() {
        return this.movementSpeedMultiplier;
    }

    @Override
    public void setMovementSpeedMultiplier(float movementSpeedMultiplier) {
        this.movementSpeedMultiplier = movementSpeedMultiplier;
    }

    @Override
    public List<PotionData> getEffects() {
        return this.effects;
    }

    @Override
    public void setEffects(List<PotionData> effects) {
        this.effects = effects;
    }

    @Override
    public float getRecoil() {
        return this.recoil;
    }

    @Override
    public void setRecoil(float recoil) {
        this.recoil = recoil;
    }
}
