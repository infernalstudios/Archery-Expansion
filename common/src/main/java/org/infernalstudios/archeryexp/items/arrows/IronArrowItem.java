package org.infernalstudios.archeryexp.items.arrows;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.entities.IronArrow;

public class IronArrowItem extends ArrowItem {

    public IronArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity entity) {
        return new IronArrow(entity, world);
    }
}
