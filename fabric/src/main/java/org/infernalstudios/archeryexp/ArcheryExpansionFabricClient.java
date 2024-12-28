package org.infernalstudios.archeryexp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.items.ArcheryItems;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.intellij.lang.annotations.Identifier;

import java.util.ArrayList;
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


        List<Item> items = List.of(ArcheryItems.Gold_Bow, ArcheryItems.Iron_Bow, ArcheryItems.Diamond_Bow, ArcheryItems.Netherite_Bow, Items.BOW);

        items.forEach(item -> {
            FabricModelPredicateProviderRegistry.register(item,
                    new ResourceLocation("drawing"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

            FabricModelPredicateProviderRegistry.register(item, new ResourceLocation("draw"), (stack, world, entity, seed) -> {
                if (entity == null || entity.getUseItem() != stack) {
                    return 0.0F;
                }

                BowProperties properties = (BowProperties) stack.getItem();

                return ArcheryExpansion.getPowerForDrawTime(stack.getUseDuration() - entity.getUseItemRemainingTicks(), properties);
            });
        });
    }
}
