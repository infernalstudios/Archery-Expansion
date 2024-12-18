package org.infernalstudios.archeryexp.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NetheriteBow extends BowItem {
    public NetheriteBow(Properties $$0) {
        super($$0);
    }

    @Override
    public void releaseUsing(ItemStack bow, Level world, LivingEntity user, int $$3) {
        super.releaseUsing(bow, world, user, $$3);

        user.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40));

        Vec3 lookDirection = user.getViewVector(1.0f);
        Vec3 knockbackVector = lookDirection.multiply(-0.5, -0.5, -0.5);

        user.setDeltaMovement(
                user.getDeltaMovement().x + knockbackVector.x,
                user.getDeltaMovement().y + knockbackVector.y,
                user.getDeltaMovement().z + knockbackVector.z
        );
        user.hurtMarked = true;

    }
}
