package org.infernalstudios.archeryexp.client.renderer;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.common.entities.arrow.MaterialArrow;
import org.jetbrains.annotations.NotNull;


public class MaterialArrowRenderer<T extends MaterialArrow> extends ArrowRenderer<T> {

    public MaterialArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return new ResourceLocation(ArcheryExpansion.MOD_ID, "textures/entity/projectiles/" + entity.getMaterial() + "_arrow.png");
    }
}
