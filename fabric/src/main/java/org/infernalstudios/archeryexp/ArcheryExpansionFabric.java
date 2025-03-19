package org.infernalstudios.archeryexp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.infernalstudios.archeryexp.client.ArrowHudThing;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.items.ArcheryItems;
import org.infernalstudios.archeryexp.items.BowStatsLoader;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingFabric;
import org.infernalstudios.archeryexp.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.particles.ArrowTrailParticle;
import org.infernalstudios.archeryexp.particles.HeadshotParticle;
import org.infernalstudios.archeryexp.particles.QuickdrawShineParticle;
import org.infernalstudios.archeryexp.registry.ArcheryEntityTypesFabric;
import org.infernalstudios.archeryexp.registry.ArcheryItemsFabric;
import org.infernalstudios.archeryexp.registry.ArcheryPariclesFabric;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;

import java.util.List;

public class ArcheryExpansionFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ArcheryItemsFabric.registerItems();
        ArcheryPariclesFabric.registerParticles();
        ArcheryEntityTypesFabric.registerEntityTypes();
        ArcheryExpansion.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ArcheryExpansionFabricReloadListener());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ArcheryExpansion.bowStatPlayerList.clear();
        });

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, table, source) -> {
            addToLootTable(table, id, BuiltInLootTables.PILLAGER_OUTPOST, ArcheryItems.Iron_Arrow, 0, 4);
            addToLootTable(table, id, BuiltInLootTables.WOODLAND_MANSION, ArcheryItems.Iron_Arrow, 0, 4);

            addToLootTable(table, id, BuiltInLootTables.BASTION_TREASURE, ArcheryItems.Gold_Arrow, 2, 7);
            addToLootTable(table, id, BuiltInLootTables.BASTION_OTHER, ArcheryItems.Gold_Arrow, 0, 5);
            addToLootTable(table, id, BuiltInLootTables.BASTION_BRIDGE, ArcheryItems.Gold_Arrow, 1, 5);

            addToLootTable(table, id, BuiltInLootTables.ANCIENT_CITY, ArcheryItems.Diamond_Arrow, 0, 3);
            addToLootTable(table, id, BuiltInLootTables.END_CITY_TREASURE, ArcheryItems.Diamond_Arrow, 0, 6);

            addToLootTable(table, id, BuiltInLootTables.BASTION_TREASURE, ArcheryItems.Netherite_Arrow, 0, 2);
        });
        }

    public class ArcheryExpansionFabricReloadListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return new ResourceLocation(ArcheryExpansion.MOD_ID, "bowstats_reload");
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            BowStatsLoader.loadBowStats(resourceManager);
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
