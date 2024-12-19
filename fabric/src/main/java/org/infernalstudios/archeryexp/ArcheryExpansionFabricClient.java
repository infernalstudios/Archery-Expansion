package org.infernalstudios.archeryexp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.entities.ArcheryEntityTypes;

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
    }
}
