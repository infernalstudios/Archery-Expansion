package org.infernalstudios.archeryexp;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.infernalstudios.archeryexp.util.json.JsonDataLoader;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingForge;
import org.infernalstudios.archeryexp.platform.ForgePlatformHelper;

@Mod(ArcheryExpansion.MOD_ID)
public class ArcheryExpansionForge {

    public ArcheryExpansionForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ArcheryExpansion.init();
        ForgePlatformHelper.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        ArcheryNetworkingForge.registerPackets();
    }

    @Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ItemGroupEvent {
        @SubscribeEvent
        public static void addCreativeTabEvent(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.COMBAT) {
                ArcheryItems.WEAPONS.forEach(supplier -> event.accept(supplier.get()));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ArcheryExpansionForgeReloadListener {
        @SubscribeEvent
        public static void onAddReloadListeners(AddReloadListenerEvent event) {
            event.addListener(new SimplePreparableReloadListener<>() {
                @Override
                protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                    return null;
                }

                @Override
                protected void apply(Object o, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                    JsonDataLoader.loadBowStats(resourceManager);
                }

                @Override
                public String getName() {
                    return "bowstats_reload";
                }
            });
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            ArcheryExpansion.BOW_STAT_PLAYER_LIST.clear();
        }

        @SubscribeEvent
        public static void onLootTableLoad(LootTableLoadEvent event) {
            addToLootTable(event, BuiltInLootTables.PILLAGER_OUTPOST, ArcheryItems.IRON_ARROW.get(), 0, 4);
            addToLootTable(event, BuiltInLootTables.WOODLAND_MANSION, ArcheryItems.IRON_ARROW.get(), 0, 4);

            addToLootTable(event, BuiltInLootTables.BASTION_TREASURE, ArcheryItems.GOLD_ARROW.get(), 2, 7);
            addToLootTable(event, BuiltInLootTables.BASTION_OTHER, ArcheryItems.GOLD_ARROW.get(), 0, 5);
            addToLootTable(event, BuiltInLootTables.BASTION_BRIDGE, ArcheryItems.GOLD_ARROW.get(), 1, 5);

            addToLootTable(event, BuiltInLootTables.ANCIENT_CITY, ArcheryItems.DIAMOND_ARROW.get(), 0, 3);
            addToLootTable(event, BuiltInLootTables.END_CITY_TREASURE, ArcheryItems.DIAMOND_ARROW.get(), 0, 6);

            addToLootTable(event, BuiltInLootTables.BASTION_TREASURE, ArcheryItems.NETHERITE_ARROW.get(), 0, 2);
        }

        private static void addToLootTable(LootTableLoadEvent event, ResourceLocation id, Item item, int min, int max) {
            if (event.getName().equals(id)) {
                LootPool arrowPool = LootPool.lootPool()
                        .name(ArcheryExpansion.MOD_ID + ":arrow_loottable_" + ForgeRegistries.ITEMS.getKey(item).getPath())
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(
                                UniformGenerator.between(min, max))
                        )).build();

                event.getTable().addPool(arrowPool);
            }
        }
    }
}