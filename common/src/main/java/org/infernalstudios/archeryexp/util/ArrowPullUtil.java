package org.infernalstudios.archeryexp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.ArcheryExpansion;

import java.util.Optional;

public class ArrowPullUtil {
    public static final ResourceLocation FALLBACK_ARROW_TEXTURE = pullTexture(ArcheryExpansion.MOD_ID, "iron_arrow");
    // Tipped Arrow Textures
    public static final ResourceLocation TIPPED_TIP = pullTexture("minecraft", "tipped_arrow_tip");
    public static final ResourceLocation TIPPED_SHAFT = pullTexture("minecraft", "tipped_arrow_shaft");

    /**
     * Returns the location of an arrow's pull texture
     * @param modID modID
     * @param arrow arrow name
     */
    public static ResourceLocation pullTexture(String modID, String arrow) {
        return new ResourceLocation(modID, "textures/arrow_pull/" + arrow + "_pulling.png");
    }

    /**
     * Returns a Pulling Texture if present, otherwise returns a Fallback Texture
     * @param item Arrow Itemstack
     */
    public static ResourceLocation getPullingTexture(ItemStack item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.getItem());
        ResourceLocation texture = ArrowPullUtil.pullTexture(id.getNamespace(), id.getPath());
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(texture);
        return resource.isPresent() ? texture : ArrowPullUtil.FALLBACK_ARROW_TEXTURE;
    }
}
