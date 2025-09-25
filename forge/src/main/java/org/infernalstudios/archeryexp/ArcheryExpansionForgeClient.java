package org.infernalstudios.archeryexp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.infernalstudios.archeryexp.client.TrajectoryRenderer;
import org.infernalstudios.archeryexp.client.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.client.particles.HeadshotParticle;
import org.infernalstudios.archeryexp.client.particles.QuickdrawShineParticle;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.infernalstudios.archeryexp.util.BowTooltipUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;

public class ArcheryExpansionForgeClient {

    @Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ArcheryEntityTypes.IRON_ARROW.get(), MaterialArrowRenderer::new);
            EntityRenderers.register(ArcheryEntityTypes.GOLD_ARROW.get(), MaterialArrowRenderer::new);
            EntityRenderers.register(ArcheryEntityTypes.DIAMOND_ARROW.get(), MaterialArrowRenderer::new);
            EntityRenderers.register(ArcheryEntityTypes.NETHERITE_ARROW.get(), MaterialArrowRenderer::new);

            ArcheryItems.BOWS.forEach(supplier -> {
                Item item = supplier.get();
                ItemProperties.register(item, new ResourceLocation("drawing"), (stack, world, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0);

                ItemProperties.register(item, new ResourceLocation("draw"), (stack, world, entity, seed) -> {
                    if (entity == null || entity.getUseItem() != stack) {
                        return 0;
                    }

                    IBowProperties properties = (IBowProperties) stack.getItem();

                    return BowUtil.getPowerForDrawTime(stack.getUseDuration() - entity.getUseItemRemainingTicks(), properties);
                });
            });

        }

        @SubscribeEvent
        public static void registerParticles(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ArcheryParticles.HEADSHOT.get(), HeadshotParticle.Factory::new);
            event.registerSpriteSet(ArcheryParticles.QUICKDRAW_SHINE.get(), QuickdrawShineParticle.Factory::new);
        }
    }

    @Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, value = Dist.CLIENT)
    public class TooltipEvents {
        @SubscribeEvent
        public static void bowTooltipEvent(ItemTooltipEvent event) {
            BowTooltipUtil.addBowTooltips(event.getToolTip(), event.getItemStack());
        }
    }

    @Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public class WorldRenderEvents {
        @SubscribeEvent
        public static void renderTrajectory(RenderLevelStageEvent event) {
            Minecraft client = Minecraft.getInstance();
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                return;
            }
            TrajectoryRenderer.render(event.getPoseStack(), client.renderBuffers().bufferSource(), client.level);
        }
    }

}
