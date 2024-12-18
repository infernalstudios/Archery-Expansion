package org.infernalstudios.archeryexp.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.items.ArcheryItems;

public class DiamondArrow extends MaterialArrow {

    public DiamondArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(ArcheryEntityTypes.Diamond_Arrow, world);
    }

    @Override
    protected double getDamageModifier() {
        return 4;
    }

    public DiamondArrow(LivingEntity owner, Level world) {
        super(ArcheryEntityTypes.Diamond_Arrow, owner, world);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ArcheryItems.Diamond_Arrow.getDefaultInstance();
    }
}
