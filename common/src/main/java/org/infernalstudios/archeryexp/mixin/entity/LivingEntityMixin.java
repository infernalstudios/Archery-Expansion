package org.infernalstudios.archeryexp.mixin.entity;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.infernalstudios.archeryexp.common.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.common.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.util.ArcheryEnchantUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void archeryexp$tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack stack = entity.getUseItem();

        boolean usingBow = entity.isUsingItem() && stack.getItem() instanceof ProjectileWeaponItem;

        ArcheryEnchantUtil.effectEnchantmentAction(ArcheryEnchants.GRIT, entity, stack, usingBow, MobEffects.DAMAGE_RESISTANCE, 5);
        ArcheryEnchantUtil.effectEnchantmentAction(ArcheryEnchants.SCOUTING, entity, stack, usingBow, MobEffects.MOVEMENT_SPEED, 30);
        ArcheryEnchantUtil.effectEnchantmentAction(ArcheryEnchants.PINCUSHIONING, entity, stack, true, ArcheryEffects.QUICKDRAW_EFFECT.get(), 30);
    }
}
