package org.infernalstudios.archeryexp.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends ItemMixin implements BowProperties {

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
    private float breakChance;
    @Unique
    private float movementSpeedMultiplier;
    @Unique
    private float recoil;
    @Unique
    private boolean hasSpecialProperties;
    @Unique
    private float offsetX;
    @Unique
    private float offsetY;
    @Unique
    private boolean hasDescText;

    @Unique
    private List<PotionData> effects = new ArrayList<>();

    @Unique
    private List<ParticleData> particles = new ArrayList<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(Item.Properties properties, CallbackInfo ci) {
        this.cooldown = 0;
        this.baseDamage = 0;
        this.range = 0;
        this.drawTime = 0;
        this.breakingResistance = 0;
        this.breakChance = 0;
        this.movementSpeedMultiplier = 0;
        this.recoil = 0;
        this.offsetX = 0;
        this.offsetY = 0;
        this.hasDescText = true;

        this.hasSpecialProperties = false;
    }

    // The weird name with the mod ID prefixed is to avoid collisions with other mods that have mixins which add methods of the same name.
    // This method was previously named "getItem".
    @Unique
    private Item archeryexp$self() {
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
            float shoot = BowUtil.getPowerForDrawTime($$7, (BowProperties) archeryexp$self());
            arrow.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0f, shoot * getRange(), 1.0f);

            double damage = checkForArrowMatch(arrow, "caverns_and_chasms:large_arrow") ? getBaseDamage() + 4.0 : getBaseDamage();
            arrow.setBaseDamage(damage);

            // Duck Tape Solution :P
            int $$12 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
            if ($$12 > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + (double)$$12 * 0.5 + 0.5);
            }

            arrow.setCritArrow(shoot == 1.0f);

            level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.SHATTERING, stack);
            ((ArrowProperties) arrow).setShatterLevel(level);

            level = EnchantmentHelper.getItemEnchantmentLevel(ArcheryEnchants.HEADSHOT, stack);
            ((ArrowProperties) arrow).setHeadshotLevel(level);

            user.getCooldowns().addCooldown(archeryexp$self(), getBowCooldown());

            this.effects.forEach(potionData -> {

                MobEffect effect = potionData.getEffect();

                if (effect != null) {
                    user.addEffect(new MobEffectInstance(potionData.getEffect(), potionData.getLength(), potionData.getLevel(),
                            potionData.getParticles(), potionData.getParticles()));
                }
            });

            this.particles.forEach(particleData -> {

                SimpleParticleType particle = particleData.getType();

                if (user.level() instanceof ServerLevel serverLevel && particle != null) {

                    Vec3 o = particleData.getPosOffset();
                    Vec3 v = particleData.getVelocity();

                    Vec3 lookVector = entity.getLookAngle();

                    Vec3 inFrontPos = entity.position().add(lookVector.scale(particleData.getLookOffset()));

                    serverLevel.sendParticles(
                            particle,
                            inFrontPos.x + o.x, user.getEyeY() + o.y, inFrontPos.z() + o.z,
                            particleData.getCount(),
                            v.x,
                            v.y,
                            v.z,
                            0.0
                    );
                }
            });

            applyRecoil(user, getRecoil());


            // Cursed Bow
            if (stack.is(Items.BOW)) {
                float breakChance = getCursedBowBreakChance(arrow);

                if (user.getRandom().nextInt(100) < breakChance) {
                    stack.hurtAndBreak(stack.getMaxDamage(), user, player ->
                            player.broadcastBreakEvent(player.getUsedItemHand()));
                }
            }
        }
    }

    @Override
    public void appendCustomAEBowText(ItemStack $$0, Level $$1, List<Component> tooltip, TooltipFlag $$3, CallbackInfo ci) {
        super.appendCustomAEBowText($$0, $$1, tooltip, $$3, ci);

        if (this.hasSpecialProperties && this.hasDescText) {

            tooltip.add(Component.literal(" "));

            tooltip.add(Component.translatable("attribute.archeryexp.mainhand").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));

            tooltip.add(Component.literal(" " + getBaseDamage() + " ")
                    .append(Component.translatable("attribute.archeryexp.base_damage")).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GREEN)));

            tooltip.add(Component.literal(" " + getBowCooldown() + " ")
                    .append(Component.translatable("attribute.archeryexp.draw_cooldown")).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GREEN)));
        }
    }

    @Unique
    private float getCursedBowBreakChance(AbstractArrow arrow) {

        EntityType<?> type = arrow.getType();

        if (type == EntityType.ARROW) {
            return 0;
        }
        else if (type == ArcheryEntityTypes.Gold_Arrow) {
            return 10;
        }
        else if (type == ArcheryEntityTypes.Iron_Arrow) {
            return 20;
        }
        else if (type == ArcheryEntityTypes.Diamond_Arrow) {
            return 40;
        }
        else if (type == ArcheryEntityTypes.Netherite_Arrow) {
            return 60;
        }
        return 12;
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
    public boolean hasSpecialProperties() {
        return this.hasSpecialProperties;
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
    public List<ParticleData> getParticles() {
        return this.particles;
    }

    @Override
    public void setParticles(List<ParticleData> particles) {
        this.particles = particles;
    }

    @Override
    public float getRecoil() {
        return this.recoil;
    }

    @Override
    public void setRecoil(float recoil) {
        this.recoil = recoil;
    }

    @Override
    public float getBreakingChance() {
        return this.breakChance;
    }

    @Override
    public void setBreakingChance(float breakChance) {
        this.breakChance = breakChance;
    }

    @Unique
    public boolean checkForArrowMatch(Entity entity, String entityId) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(new ResourceLocation(entityId));
    }

    @Override
    public float getOffsetX() {
        return this.offsetX;
    }

    @Override
    public void setOffsetX(float x) {
        this.offsetX = x;
    }

    @Override
    public float getOffsetY() {
        return this.offsetY;
    }

    @Override
    public void setOffsetY(float y) {
        this.offsetY = y;
    }

    @Override
    public void setHasDescText(boolean hasDescText) {
        this.hasDescText = hasDescText;
    }

    @Override
    public boolean hasDescText() {
        return this.hasDescText;
    }
}
