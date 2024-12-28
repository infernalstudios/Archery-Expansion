package org.infernalstudios.archeryexp;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.items.ArcheryItems;
import org.infernalstudios.archeryexp.items.BowStatsLoader;
import org.infernalstudios.archeryexp.platform.ForgePlatformHelper;
import org.infernalstudios.archeryexp.registry.ArcheryEntityTypesForge;
import org.infernalstudios.archeryexp.registry.ArcheryItemsForge;
import org.infernalstudios.archeryexp.util.BowProperties;

import java.util.List;

@Mod(ArcheryExpansion.MOD_ID)
public class ArcheryExpansionForge {

    public ArcheryExpansionForge() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ArcheryItemsForge.registerItems(modEventBus);

        ArcheryEntityTypesForge.registerEntities(modEventBus);

        ForgePlatformHelper.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        ArcheryExpansion.init();
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        ArcheryItemsForge.registerItemsCommon();
        ArcheryEntityTypesForge.registerEntitiesCommon();
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

            List<Item> items = List.of(ArcheryItems.Gold_Bow, ArcheryItems.Iron_Bow, ArcheryItems.Diamond_Bow, ArcheryItems.Netherite_Bow, Items.BOW);

            items.forEach(item -> {
                ItemProperties.register(item,
                        new ResourceLocation("drawing"), (stack, world, entity, seed) ->
                                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

                ItemProperties.register(item, new ResourceLocation("draw"), (stack, world, entity, seed) -> {
                    if (entity == null || entity.getUseItem() != stack) {
                        return 0.0F;
                    }

                    BowProperties properties = (BowProperties) stack.getItem();

                    return ArcheryExpansion.getPowerForDrawTime(stack.getUseDuration() - entity.getUseItemRemainingTicks(), properties);
                });
            });
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
    }
}