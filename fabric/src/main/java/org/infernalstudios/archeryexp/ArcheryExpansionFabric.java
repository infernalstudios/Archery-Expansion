package org.infernalstudios.archeryexp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.items.ArcheryItems;
import org.infernalstudios.archeryexp.items.BowStatsLoader;
import org.infernalstudios.archeryexp.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.particles.ArrowTrailParticle;
import org.infernalstudios.archeryexp.particles.HeadshotParticle;
import org.infernalstudios.archeryexp.registry.ArcheryEntityTypesFabric;
import org.infernalstudios.archeryexp.registry.ArcheryItemsFabric;
import org.infernalstudios.archeryexp.registry.ArcheryPariclesFabric;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;

import java.util.List;

public class ArcheryExpansionFabric implements ModInitializer, ClientModInitializer {
    
    @Override
    public void onInitialize() {
        ArcheryItemsFabric.registerItems();
        ArcheryPariclesFabric.registerParticles();
        ArcheryEntityTypesFabric.registerEntityTypes();
        ArcheryExpansion.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ArcheryExpansionFabricReloadListener());
    }

    @Override
    public void onInitializeClient() {
        String path = "textures/entity/projectiles/";
        EntityRendererRegistry.register(ArcheryEntityTypes.Iron_Arrow, (ctx) ->
                new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "iron_arrow.png")));
        EntityRendererRegistry.register(ArcheryEntityTypes.Gold_Arrow, (ctx) ->
                new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "golden_arrow.png")));
        EntityRendererRegistry.register(ArcheryEntityTypes.Diamond_Arrow, (ctx) ->
                new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "diamond_arrow.png")));


        List<Item> items = List.of(ArcheryItems.Gold_Bow, ArcheryItems.Iron_Bow, ArcheryItems.Diamond_Bow, ArcheryItems.Netherite_Bow,
                ArcheryItems.Wooden_Bow, Items.BOW);

        items.forEach(item -> {
            FabricModelPredicateProviderRegistry.register(item,
                    new ResourceLocation("drawing"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

            FabricModelPredicateProviderRegistry.register(item, new ResourceLocation("draw"), (stack, world, entity, seed) -> {
                if (entity == null || entity.getUseItem() != stack) {
                    return 0.0F;
                }

                BowProperties properties = (BowProperties) stack.getItem();

                return BowUtil.getPowerForDrawTime(stack.getUseDuration() - entity.getUseItemRemainingTicks(), properties);
            });
        });

        ParticleFactoryRegistry.getInstance().register(ArcheryParticles.ARROW_TRAIL, ArrowTrailParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ArcheryParticles.HEADSHOT, HeadshotParticle.Factory::new);
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
}
