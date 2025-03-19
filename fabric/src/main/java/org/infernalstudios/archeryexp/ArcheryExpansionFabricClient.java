package org.infernalstudios.archeryexp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.infernalstudios.archeryexp.client.ArrowHudThing;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.items.ArcheryItems;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingFabric;
import org.infernalstudios.archeryexp.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.particles.ArrowTrailParticle;
import org.infernalstudios.archeryexp.particles.HeadshotParticle;
import org.infernalstudios.archeryexp.particles.QuickdrawShineParticle;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;

import java.util.List;

public class ArcheryExpansionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        String path = "textures/entity/projectiles/";
        EntityRendererRegistry.register(ArcheryEntityTypes.Iron_Arrow, (ctx) ->
                new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "iron_arrow.png")));
        EntityRendererRegistry.register(ArcheryEntityTypes.Gold_Arrow, (ctx) ->
                new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "golden_arrow.png")));
        EntityRendererRegistry.register(ArcheryEntityTypes.Diamond_Arrow, (ctx) ->
                new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "diamond_arrow.png")));
        EntityRendererRegistry.register(ArcheryEntityTypes.Netherite_Arrow, (ctx) ->
                new MaterialArrowRenderer(ctx, new ResourceLocation(ArcheryExpansion.MOD_ID, path + "netherite_arrow.png")));


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
        ParticleFactoryRegistry.getInstance().register(ArcheryParticles.QUICKDRAW_SHINE, QuickdrawShineParticle.Factory::new);

        HudRenderCallback.EVENT.register(ArrowHudThing::renderBowBar);
        ArcheryNetworkingFabric.registerS2CPackets();
    }
}
