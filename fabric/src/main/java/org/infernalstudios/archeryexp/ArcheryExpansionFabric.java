package org.infernalstudios.archeryexp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.infernalstudios.archeryexp.util.json.JsonDataLoader;

public class ArcheryExpansionFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ArcheryExpansion.init();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries ->
                ArcheryItems.WEAPONS.forEach(supplier -> entries.accept(supplier.get())));

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ArcheryExpansionFabricReloadListener());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ArcheryExpansion.BOW_STAT_PLAYER_LIST.clear());

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, table, source) -> {
            addToLootTable(table, id, BuiltInLootTables.PILLAGER_OUTPOST, ArcheryItems.IRON_ARROW.get(), 0, 4);
            addToLootTable(table, id, BuiltInLootTables.WOODLAND_MANSION, ArcheryItems.IRON_ARROW.get(), 0, 4);

            addToLootTable(table, id, BuiltInLootTables.BASTION_TREASURE, ArcheryItems.GOLD_ARROW.get(), 2, 7);
            addToLootTable(table, id, BuiltInLootTables.BASTION_OTHER, ArcheryItems.GOLD_ARROW.get(), 0, 5);
            addToLootTable(table, id, BuiltInLootTables.BASTION_BRIDGE, ArcheryItems.GOLD_ARROW.get(), 1, 5);

            addToLootTable(table, id, BuiltInLootTables.ANCIENT_CITY, ArcheryItems.DIAMOND_ARROW.get(), 0, 3);
            addToLootTable(table, id, BuiltInLootTables.END_CITY_TREASURE, ArcheryItems.DIAMOND_ARROW.get(), 0, 6);

            addToLootTable(table, id, BuiltInLootTables.BASTION_TREASURE, ArcheryItems.NETHERITE_ARROW.get(), 0, 2);
        });
    }

    public static class ArcheryExpansionFabricReloadListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return new ResourceLocation(ArcheryExpansion.MOD_ID, "bowstats_reload");
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            JsonDataLoader.loadBowStats(resourceManager);
        }
    }

    private static void addToLootTable(LootTable.Builder tableBuilder, ResourceLocation id, ResourceLocation lootTable, Item item, int min, int max) {
        if (lootTable.equals(id)) {
            LootPool.Builder arrowPool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(
                            UniformGenerator.between(min, max))
                    ));

            tableBuilder.pool(arrowPool.build());
        }
    }
}
