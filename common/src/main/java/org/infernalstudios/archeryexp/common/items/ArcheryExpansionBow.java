package org.infernalstudios.archeryexp.common.items;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArcheryExpansionBow extends BowItem {

    private final Item repairItem;

    public ArcheryExpansionBow(Properties properties, Item repairItem) {
        super(properties);
        this.repairItem = repairItem;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairStack.is(this.repairItem);
    }
}
