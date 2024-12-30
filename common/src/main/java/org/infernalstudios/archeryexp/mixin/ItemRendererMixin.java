package org.infernalstudios.archeryexp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.client.MockItemRenderer;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V",
            shift = At.Shift.AFTER
        )
    )
    public void renderItem(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int $$6, BakedModel model, CallbackInfo ci) {

        Player player = Minecraft.getInstance().player;

        if (stack.getItem() instanceof BowItem && ((BowProperties) stack.getItem()).hasSpecialProperties() &&
                player.isUsingItem() && player.getUseItem() == stack) {

            ItemStack arrow = player.getProjectile(stack);

            if (!arrow.isEmpty()) {
                float pull = ArcheryExpansion.getPowerForDrawTime(stack.getUseDuration() - player.getUseItemRemainingTicks(), (BowProperties) stack.getItem());

                float posOffset = pull >= 0.9f ? 0.125f : (pull >= 0.65f ? 0.0625f : 0);

                poseStack.pushPose();

                poseStack.scale(-1.01f, -1.01f, -1.01f);

                poseStack.translate(-0.995f - posOffset, -0.995f + posOffset, -0.495f);

                if (arrow.is(Items.TIPPED_ARROW)) {

                    int color = PotionUtils.getColor(arrow);

                    ResourceLocation tipTex = new ResourceLocation("textures/arrow_pull/tipped_arrow_pulling_tip.png");
                    ResourceLocation shaftTex = new ResourceLocation("textures/arrow_pull/tipped_arrow_pulling_shaft.png");

                    MockItemRenderer.PixelData[][] tipPixels =
                            MockItemRenderer.loadPixelData(tipTex, 16);

                    MockItemRenderer.renderExtrudedSprite(tipPixels, 0.065f, poseStack, bufferSource, light, tipTex, color);

                    MockItemRenderer.PixelData[][] shaftPixels =
                            MockItemRenderer.loadPixelData(shaftTex, 16);

                    MockItemRenderer.renderExtrudedSprite(shaftPixels, 0.065f, poseStack, bufferSource, light, shaftTex, -1);
                }
                else {
                    ResourceLocation arrowTex = getArrowTexture(arrow);

                    MockItemRenderer.PixelData[][] arrowPixels =
                            MockItemRenderer.loadPixelData(arrowTex, 16);

                    MockItemRenderer.renderExtrudedSprite(arrowPixels, 0.065f, poseStack, bufferSource, light, arrowTex, -1);
                }

                poseStack.popPose();
            }
        }
    }

    @Unique
    private ResourceLocation getArrowTexture(ItemStack arrow) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        ResourceLocation arrowResource = BuiltInRegistries.ITEM.getKey(arrow.getItem());

        ResourceLocation arrowTex = new ResourceLocation(arrowResource.getNamespace(),
                "textures/arrow_pull/" + arrowResource.getPath() + "_pulling.png");

        Optional<Resource> resource = resourceManager.getResource(arrowTex);

        if (resource.isPresent()) {
            return arrowTex;
        }
        return new ResourceLocation("textures/arrow_pull/arrow_pulling.png");
    }
}
