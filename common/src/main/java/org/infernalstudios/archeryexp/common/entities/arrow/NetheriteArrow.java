package org.infernalstudios.archeryexp.common.entities.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.jetbrains.annotations.NotNull;

public class NetheriteArrow extends MaterialArrow {

    public NetheriteArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(ArcheryEntityTypes.NETHERITE_ARROW.get(), world);
    }

    public NetheriteArrow(LivingEntity owner, Level world) {
        super(ArcheryEntityTypes.NETHERITE_ARROW.get(), owner, world);
    }

    @Override
    protected double getDamageModifier() {
        return 5;
    }

    @Override
    public String getMaterial() {
        return "netherite";
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ArcheryItems.NETHERITE_ARROW.get().getDefaultInstance();
    }
}
