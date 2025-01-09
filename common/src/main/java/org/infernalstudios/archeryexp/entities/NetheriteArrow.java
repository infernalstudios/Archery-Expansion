package org.infernalstudios.archeryexp.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.items.ArcheryItems;

public class NetheriteArrow extends MaterialArrow {

    public NetheriteArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(ArcheryEntityTypes.Netherite_Arrow, world);
    }

    @Override
    protected double getDamageModifier() {
        return 5;
    }

    public NetheriteArrow(LivingEntity owner, Level world) {
        super(ArcheryEntityTypes.Netherite_Arrow, owner, world);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ArcheryItems.Netherite_Arrow.getDefaultInstance();
    }
}
