package org.infernalstudios.archeryexp.items;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.ArcheryExpansion;

public class ArcheryExpansionItems {



    //Registration stuff
    private static Item registerItem(String itemName, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(ArcheryExpansion.MOD_ID, itemName), item);
    }

    public static void registerItems() {
        ArcheryExpansion.LOGGER.debug("Registering Items for" + ArcheryExpansion.MOD_ID);
    }
}
