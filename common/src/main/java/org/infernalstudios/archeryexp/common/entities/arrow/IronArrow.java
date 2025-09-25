package org.infernalstudios.archeryexp.common.entities.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;

public class IronArrow extends MaterialArrow {

    public IronArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(ArcheryEntityTypes.GOLD_ARROW.get(), world);
    }
    public IronArrow(LivingEntity owner, Level world) {
        super(ArcheryEntityTypes.GOLD_ARROW.get(), owner, world);
    }

    @Override
    protected double getDamageModifier() {
        return 2;
    }

    @Override
    public String getMaterial() {
        return "iron";
    }

    @Override
    protected ItemStack getPickupItem() {
        return ArcheryItems.IRON_ARROW.get().getDefaultInstance();
    }
}
