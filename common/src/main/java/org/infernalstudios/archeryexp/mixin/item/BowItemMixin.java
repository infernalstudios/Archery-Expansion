package org.infernalstudios.archeryexp.mixin.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.util.*;
import org.infernalstudios.archeryexp.util.json.data.BowParticleData;
import org.infernalstudios.archeryexp.util.json.data.BowEffectData;
import org.infernalstudios.archeryexp.util.mixinterfaces.IArrowProperties;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(BowItem.class)
public abstract class BowItemMixin implements IBowProperties {

    @Unique private List<BowEffectData> archeryexp$effects = new ArrayList<>();
    @Unique private List<BowParticleData> archeryexp$particles = new ArrayList<>();
    
    @Unique private int archeryexp$cooldown, archeryexp$drawTime;
    @Unique private float archeryexp$range, archeryexp$baseDamage, archeryexp$breakingResistance, archeryexp$breakChance,
            archeryexp$walkSpeed, archeryexp$recoil, archeryexp$offsetX, archeryexp$offsetY;
    @Unique private boolean archeryexp$special;
    @Unique private boolean archeryexp$hasDescText = true;
    

    @Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void archeryexp$releaseUsing(ItemStack stack, Level world, LivingEntity entity, int remainingUseTicks, CallbackInfo ci, Player user, boolean $$5, ItemStack $$6, int $$7, float $$8, boolean $$9, ArrowItem arrowItem, AbstractArrow arrow) {
        BowItem bow = (BowItem) (Object) this;

        ArcheryEnchantUtil.enchantmentAction(() -> Enchantments.POWER_ARROWS, user, stack, true, (lvl) -> stack.hurtAndBreak(lvl, user, (ignore) -> {}));

        if (this.archeryexp$special) {
            float shoot = BowUtil.getPowerForDrawTime($$7, this);
            arrow.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0f, shoot * archeryexp$getRange(), 1.0f);

            double damage = archeryexp$checkModdedArrow(arrow, "caverns_and_chasms:large_arrow") ? archeryexp$getBaseDamage() + 4.0 : archeryexp$getBaseDamage();
            arrow.setBaseDamage(damage);

            // Duck Tape Solution :P
            ArcheryEnchantUtil.enchantmentAction(() -> Enchantments.POWER_ARROWS, user, stack, true, (lvl) -> 
                    arrow.setBaseDamage(arrow.getBaseDamage() + (double)lvl * 0.5 + 0.5));
            
            arrow.setCritArrow(shoot == 1.0f);

            ((IArrowProperties) arrow).archeryexp$setShatterLevel(stack);
            ((IArrowProperties) arrow).archeryexp$setHeadshotLevel(stack);
            
            this.archeryexp$effects.forEach(effect -> effect.apply(user));
            this.archeryexp$particles.forEach(particle -> particle.apply(user));

            user.getCooldowns().addCooldown(bow, archeryexp$getBowCooldown());
            archeryexp$applyRecoil(user, archeryexp$getRecoil());


            // Cursed Bow
            if (stack.is(Items.BOW) && user.getRandom().nextInt(100) < archeryexp$oldBowBreakChance(arrow)) {
                stack.hurtAndBreak(stack.getMaxDamage(), user, player -> player.broadcastBreakEvent(player.getUsedItemHand()));
            }
        }
    }

    @Unique
    private float archeryexp$oldBowBreakChance(AbstractArrow arrow) {

        EntityType<?> type = arrow.getType();

        if (type == EntityType.ARROW) {
            return 0;
        }
        else if (type == ArcheryEntityTypes.GOLD_ARROW) {
            return 10;
        }
        else if (type == ArcheryEntityTypes.IRON_ARROW) {
            return 20;
        }
        else if (type == ArcheryEntityTypes.DIAMOND_ARROW) {
            return 40;
        }
        else if (type == ArcheryEntityTypes.NETHERITE_ARROW) {
            return 60;
        }
        return 12;
    }

    @Unique
    private void archeryexp$applyRecoil(Player user, double amount) {
        Vec3 lookDirection = user.getViewVector(1.0f);
        Vec3 knockbackVector = lookDirection.multiply(-amount, -amount, -amount);
        user.setDeltaMovement(
                user.getDeltaMovement().x + knockbackVector.x,
                user.getDeltaMovement().y + knockbackVector.y,
                user.getDeltaMovement().z + knockbackVector.z
        );
        user.hurtMarked = true;
    }

    @Unique
    public boolean archeryexp$checkModdedArrow(Entity entity, String entityId) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(new ResourceLocation(entityId));
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // GETTERS AND SETTERS

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public int archeryexp$getBowCooldown() {return this.archeryexp$cooldown;}
    @Override public void archeryexp$setBowCooldown(int cooldown) {this.archeryexp$cooldown = cooldown;}

    
    @Override
    public float archeryexp$getBaseDamage() {return this.archeryexp$baseDamage;}
    @Override
    public void archeryexp$setBaseDamage(float baseDamage) {this.archeryexp$baseDamage = baseDamage;}

    
    @Override public float archeryexp$getRange() {return this.archeryexp$range;}
    @Override public void archeryexp$setRange(float range) {this.archeryexp$range = range;}

    
    @Override public int archeryexp$getChargeTime() {return this.archeryexp$drawTime;}
    @Override public void archeryexp$setChargeTime(int chargeTime) {this.archeryexp$drawTime = chargeTime;}

    
    @Override public boolean archeryexp$isSpecial() {return this.archeryexp$special;}
    @Override public void archeryexp$setSpecial(boolean hasProperties) {this.archeryexp$special = hasProperties;}

    
    @Override public void archeryexp$setBreakResist(float breakResist) {this.archeryexp$breakingResistance = breakResist;}
    @Override public float archeryexp$getBreakResist() {return !this.archeryexp$special ? 1 : this.archeryexp$breakingResistance;}

    
    @Override public float archeryexp$getWalkSpeed() {return this.archeryexp$walkSpeed;}
    @Override public void archeryexp$setWalkSpeed(float walkSpeed) {this.archeryexp$walkSpeed = walkSpeed;}

    
    @Override public List<BowEffectData> archeryexp$getEffects() {return this.archeryexp$effects;}
    @Override public void archeryexp$setEffects(List<BowEffectData> effects) {this.archeryexp$effects = effects;}

    
    @Override
    public List<BowParticleData> archeryexp$getParticles() {return this.archeryexp$particles;}
    @Override public void archeryexp$setParticles(List<BowParticleData> particles) {this.archeryexp$particles = particles;}

    
    @Override public float archeryexp$getRecoil() {return this.archeryexp$recoil;}
    @Override public void archeryexp$setRecoil(float recoil) {this.archeryexp$recoil = recoil;}


    @Override public float archeryexp$getBreakChance() {return this.archeryexp$breakChance;}
    @Override public void archeryexp$setBreakChance(float breakChance) {this.archeryexp$breakChance = breakChance;}

    
    @Override public float archeryexp$getOffsetX() {return this.archeryexp$offsetX;}
    @Override public void archeryexp$setOffsetX(float x) {this.archeryexp$offsetX = x;}

    
    @Override public float archeryexp$getOffsetY() {return this.archeryexp$offsetY;}
    @Override public void archeryexp$setOffsetY(float y) {this.archeryexp$offsetY = y;}


    @Override public boolean archeryexp$hasDesc() {return this.archeryexp$hasDescText;}
    @Override public void archeryexp$setHasDesc(boolean hasDescText) {this.archeryexp$hasDescText = hasDescText;}
}
