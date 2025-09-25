package org.infernalstudios.archeryexp.common.entities.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.jetbrains.annotations.NotNull;

public class DiamondArrow extends MaterialArrow {

    public DiamondArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(ArcheryEntityTypes.DIAMOND_ARROW.get(), world);
    }
    public DiamondArrow(LivingEntity owner, Level world) {
        super(ArcheryEntityTypes.DIAMOND_ARROW.get(), owner, world);
    }

    @Override
    protected double getDamageModifier() {
        return 4;
    }

    @Override
    public String getMaterial() {
        return "diamond";
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ArcheryItems.DIAMOND_ARROW.get().getDefaultInstance();
    }
}
