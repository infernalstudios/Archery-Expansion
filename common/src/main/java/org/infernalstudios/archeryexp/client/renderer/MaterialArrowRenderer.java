package org.infernalstudios.archeryexp.client.renderer;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;


public class MaterialArrowRenderer extends ArrowRenderer {

    public final ResourceLocation texture;

    public MaterialArrowRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return this.texture;
    }
}
