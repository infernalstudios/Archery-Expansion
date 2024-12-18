package org.infernalstudios.archeryexp.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.items.ArcheryItems;

public class IronArrow extends MaterialArrow {

    public IronArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(ArcheryEntityTypes.Iron_Arrow, world);
    }

    @Override
    protected double getDamageModifier() {
        return 2;
    }

    public IronArrow(LivingEntity owner, Level world) {
        super(ArcheryEntityTypes.Iron_Arrow, owner, world);
    }
    
    @Override
    protected ItemStack getPickupItem() {
        return ArcheryItems.Iron_Arrow.getDefaultInstance();
    }
}
