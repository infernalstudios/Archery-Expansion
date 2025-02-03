package org.infernalstudios.archeryexp;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.infernalstudios.archeryexp.client.ArrowHudThing;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.items.ArcheryItems;
import org.infernalstudios.archeryexp.items.BowStatsLoader;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingForge;
import org.infernalstudios.archeryexp.particles.ArrowTrailParticle;
import org.infernalstudios.archeryexp.particles.HeadshotParticle;
import org.infernalstudios.archeryexp.particles.QuickdrawShineParticle;
import org.infernalstudios.archeryexp.platform.ForgePlatformHelper;
import org.infernalstudios.archeryexp.registry.ArcheryEntityTypesForge;
import org.infernalstudios.archeryexp.registry.ArcheryItemsForge;
import org.infernalstudios.archeryexp.registry.ArcheryParticlesForge;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;

import java.util.List;

@Mod(ArcheryExpansion.MOD_ID)
public class ArcheryExpansionForge {

    public ArcheryExpansionForge() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ArcheryItemsForge.registerItems(modEventBus);

        ArcheryEntityTypesForge.registerEntities(modEventBus);

        ForgePlatformHelper.register(modEventBus);

        ArcheryParticlesForge.registerParticles(modEventBus);
        modEventBus.addListener(this::commonSetup);
        ArcheryExpansion.init();
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        ArcheryItemsForge.registerItemsCommon();
        ArcheryParticlesForge.registerParticlesCommon();
        ArcheryEntityTypesForge.registerEntitiesCommon();
        ArcheryNetworkingForge.registerPackets();
    }

    @Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            String path = "textures/entity/projectiles/";
            EntityRenderers.register(ArcheryEntityTypesForge.Iron_Arrow.get(), (ctx) ->
                    new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "iron_arrow.png")));
            EntityRenderers.register(ArcheryEntityTypesForge.Gold_Arrow.get(), (ctx) ->
                    new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "golden_arrow.png")));
            EntityRenderers.register(ArcheryEntityTypesForge.Diamond_Arrow.get(), (ctx) ->
                    new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "diamond_arrow.png")));
            EntityRenderers.register(ArcheryEntityTypesForge.Netherite_Arrow.get(), (ctx) ->
                    new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "netherite_arrow.png")));

            List<Item> items = List.of(ArcheryItems.Gold_Bow, ArcheryItems.Iron_Bow, ArcheryItems.Diamond_Bow, ArcheryItems.Netherite_Bow,
                    ArcheryItems.Wooden_Bow, Items.BOW);

            items.forEach(item -> {
                ItemProperties.register(item,
                        new ResourceLocation("drawing"), (stack, world, entity, seed) ->
                                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

                ItemProperties.register(item, new ResourceLocation("draw"), (stack, world, entity, seed) -> {
                    if (entity == null || entity.getUseItem() != stack) {
                        return 0.0F;
                    }

                    BowProperties properties = (BowProperties) stack.getItem();

                    return BowUtil.getPowerForDrawTime(stack.getUseDuration() - entity.getUseItemRemainingTicks(), properties);
                });
            });
        }

        @SubscribeEvent
        public static void registerParticles(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ArcheryParticlesForge.ARROW_TRAIL.get(), ArrowTrailParticle.Factory::new);
            event.registerSpriteSet(ArcheryParticlesForge.HEADSHOT.get(), HeadshotParticle.Factory::new);
            event.registerSpriteSet(ArcheryParticlesForge.QUICKDRAW_SHINE.get(), QuickdrawShineParticle.Factory::new);
        }
    }

    @Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class HUDClient {
        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiEvent event) {
            ArrowHudThing.renderBowBar(event.getGuiGraphics(), event.getPartialTick());
        }
    }

    @Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ArcheryExpansionForgeReloadListener {
        @SubscribeEvent
        public static void onAddReloadListeners(AddReloadListenerEvent event) {
            event.addListener(new SimplePreparableReloadListener() {
                @Override
                protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                    return null;
                }

                @Override
                protected void apply(Object o, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                    BowStatsLoader.loadBowStats(resourceManager);
                }

                @Override
                public String getName() {
                    return "bowstats_reload";
                }
            });
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            ArcheryExpansion.bowStatPlayerList.clear();
        }

        @SubscribeEvent
        public static void onLootTableLoad(LootTableLoadEvent event) {
            addToLootTable(event, BuiltInLootTables.PILLAGER_OUTPOST, ArcheryItems.Iron_Arrow, 0, 4);
            addToLootTable(event, BuiltInLootTables.WOODLAND_MANSION, ArcheryItems.Iron_Arrow, 0, 4);

            addToLootTable(event, BuiltInLootTables.BASTION_TREASURE, ArcheryItems.Gold_Arrow, 2, 7);
            addToLootTable(event, BuiltInLootTables.BASTION_OTHER, ArcheryItems.Gold_Arrow, 0, 5);
            addToLootTable(event, BuiltInLootTables.BASTION_BRIDGE, ArcheryItems.Gold_Arrow, 1, 5);

            addToLootTable(event, BuiltInLootTables.ANCIENT_CITY, ArcheryItems.Diamond_Arrow, 0, 3);
            addToLootTable(event, BuiltInLootTables.END_CITY_TREASURE, ArcheryItems.Diamond_Arrow, 0, 6);

            addToLootTable(event, BuiltInLootTables.BASTION_TREASURE, ArcheryItems.Netherite_Arrow, 0, 2);
        }

        private static void addToLootTable(LootTableLoadEvent event, ResourceLocation id, Item item, int min, int max) {
            if (event.getName().equals(id)) {
                LootPool arrowPool = LootPool.lootPool()
                        .name("my_arrow_pool")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(
                                UniformGenerator.between(min, max))
                        )).build();

                event.getTable().addPool(arrowPool);
            }
        }
    }
}