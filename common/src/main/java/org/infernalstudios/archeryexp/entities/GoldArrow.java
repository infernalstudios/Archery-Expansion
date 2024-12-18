package org.infernalstudios.archeryexp.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.items.ArcheryItems;

public class GoldArrow extends MaterialArrow {

    public GoldArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(ArcheryEntityTypes.Gold_Arrow, world);
    }

    @Override
    protected double getDamageModifier() {
        return 1;
    }

    public GoldArrow(LivingEntity owner, Level world) {
        super(ArcheryEntityTypes.Gold_Arrow, owner, world);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ArcheryItems.Gold_Arrow.getDefaultInstance();
    }
}
