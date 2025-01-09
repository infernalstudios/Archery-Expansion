package org.infernalstudios.archeryexp.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.items.arrows.DiamondArrowItem;
import org.infernalstudios.archeryexp.items.arrows.GoldArrowItem;
import org.infernalstudios.archeryexp.items.arrows.IronArrowItem;

import static org.infernalstudios.archeryexp.items.ArcheryItems.*;

public class ArcheryItemsFabric {

    //Creative Tab
    public static void addCombat(FabricItemGroupEntries entry) {
        entry.accept(Gold_Bow);
        entry.accept(Iron_Bow);
        entry.accept(Diamond_Bow);
        entry.accept(Netherite_Bow);
        entry.accept(Wooden_Bow);

        entry.accept(Gold_Arrow);
        entry.accept(Iron_Arrow);
        entry.accept(Diamond_Arrow);
        entry.accept(Netherite_Arrow);
    }

    //Registration stuff
    private static Item registerItem(String itemName, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(ArcheryExpansion.MOD_ID, itemName), item);
    }

    public static void registerItems() {

        // BOWS

        Gold_Bow = registerItem("gold_bow",
                new BowItem(new Item.Properties().stacksTo(1).durability(80)));

        Iron_Bow = registerItem("iron_bow",
                new BowItem(new Item.Properties().stacksTo(1).durability(240)));

        Diamond_Bow = registerItem("diamond_bow",
                new BowItem(new Item.Properties().stacksTo(1).durability(528)));

        Netherite_Bow = registerItem("netherite_bow",
                new BowItem(new Item.Properties().stacksTo(1).durability(592).fireResistant()));

        Wooden_Bow = registerItem("wooden_bow",
                new BowItem(new Item.Properties().stacksTo(1).durability(112)));

        // ARROWS

        Gold_Arrow = registerItem("gold_arrow",
                new GoldArrowItem(new Item.Properties()));

        Iron_Arrow = registerItem("iron_arrow",
                new IronArrowItem(new Item.Properties()));

        Diamond_Arrow = registerItem("diamond_arrow",
                new DiamondArrowItem(new Item.Properties()));

        Netherite_Arrow = registerItem("netherite_arrow",
                new DiamondArrowItem(new Item.Properties()));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(ArcheryItemsFabric::addCombat);

        ArcheryExpansion.LOGGER.debug("Registering Items for" + ArcheryExpansion.MOD_ID);
    }
}

