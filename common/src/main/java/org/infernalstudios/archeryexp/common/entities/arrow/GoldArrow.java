package org.infernalstudios.archeryexp.common.entities.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.jetbrains.annotations.NotNull;

public class GoldArrow extends MaterialArrow {

    public GoldArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(ArcheryEntityTypes.GOLD_ARROW.get(), world);
    }

    public GoldArrow(LivingEntity owner, Level world) {
        super(ArcheryEntityTypes.GOLD_ARROW.get(), owner, world);
    }

    @Override
    protected double getDamageModifier() {
        return 1;
    }

    @Override
    public String getMaterial() {
        return "golden";
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ArcheryItems.GOLD_ARROW.get().getDefaultInstance();
    }
}
