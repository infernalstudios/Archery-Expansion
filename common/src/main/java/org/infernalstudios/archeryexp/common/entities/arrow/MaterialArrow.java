package org.infernalstudios.archeryexp.common.entities.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;

public abstract class MaterialArrow extends AbstractArrow {

    protected MaterialArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(type, world);
    }
    protected MaterialArrow(EntityType<? extends AbstractArrow> type, LivingEntity user, Level world) {
        super(type, user, world);
    }

    @Override
    public void setBaseDamage(double $$0) {
        super.setBaseDamage($$0 + getDamageModifier());
    }

    protected abstract double getDamageModifier();

    public abstract String getMaterial();
}
