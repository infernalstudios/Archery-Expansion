package org.infernalstudios.archeryexp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.resources.ResourceLocation;
import org.infernalstudios.archeryexp.client.TrajectoryRenderer;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingFabric;
import org.infernalstudios.archeryexp.client.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.client.particles.HeadshotParticle;
import org.infernalstudios.archeryexp.client.particles.QuickdrawShineParticle;
import org.infernalstudios.archeryexp.util.BowTooltipUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;

public class ArcheryExpansionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ArcheryEntityTypes.IRON_ARROW.get(), MaterialArrowRenderer::new);
        EntityRendererRegistry.register(ArcheryEntityTypes.GOLD_ARROW.get(), MaterialArrowRenderer::new);
        EntityRendererRegistry.register(ArcheryEntityTypes.DIAMOND_ARROW.get(), MaterialArrowRenderer::new);
        EntityRendererRegistry.register(ArcheryEntityTypes.NETHERITE_ARROW.get(), MaterialArrowRenderer::new);

        ArcheryItems.BOWS.forEach(item -> {
            FabricModelPredicateProviderRegistry.register(item.get(), new ResourceLocation("drawing"),
                    (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0);

            FabricModelPredicateProviderRegistry.register(item.get(), new ResourceLocation("draw"), (stack, world, entity, seed) -> {
                if (entity == null || entity.getUseItem() != stack) {
                    return 0;
                }

                IBowProperties properties = (IBowProperties) stack.getItem();
                return BowUtil.getPowerForDrawTime(stack.getUseDuration() - entity.getUseItemRemainingTicks(), properties);
            });
        });

        ParticleFactoryRegistry.getInstance().register(ArcheryParticles.HEADSHOT.get(), HeadshotParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ArcheryParticles.QUICKDRAW_SHINE.get(), QuickdrawShineParticle.Factory::new);

        ArcheryNetworkingFabric.registerS2CPackets();

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> BowTooltipUtil.addBowTooltips(lines, stack));
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            TrajectoryRenderer.render(context.matrixStack(), context.consumers(), context.world());
        });
    }
}
