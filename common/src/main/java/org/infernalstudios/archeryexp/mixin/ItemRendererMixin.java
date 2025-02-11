package org.infernalstudios.archeryexp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.client.MockItemRenderer;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;
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

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level != null) {
            for (Entity entity : minecraft.level.entitiesForRendering()) {
                if (entity instanceof LivingEntity user) {
                    if (stack.getItem() instanceof BowItem && ((BowProperties) stack.getItem()).hasSpecialProperties() &&
                            user.isUsingItem() && user.getUseItem() == stack) {
                        BowProperties bowProp = (BowProperties) stack.getItem();

                        ItemStack arrow = user.getProjectile(stack);

                        if (!arrow.isEmpty()) {
                            float pull = BowUtil.getPowerForDrawTime(stack.getUseDuration() - user.getUseItemRemainingTicks(), bowProp);

                            float posOffset = pull >= 0.9f ? 0.125f : (pull >= 0.65f ? 0.0625f : 0);

                            poseStack.pushPose();

                            poseStack.scale(1.01f, 1.01f, 1.01f);
                            poseStack.translate(0.995f + posOffset - (0.125 * -bowProp.getOffsetX()), 0.995f - posOffset - (0.125 * bowProp.getOffsetY()), 0.495f);
                            poseStack.mulPose(Axis.ZP.rotationDegrees(180));

                            if (arrow.is(Items.TIPPED_ARROW)) {

                                int color = PotionUtils.getColor(arrow);

                                ResourceLocation tipTex = new ResourceLocation("textures/arrow_pull/tipped_arrow_pulling_tip.png");
                                ResourceLocation shaftTex = new ResourceLocation("textures/arrow_pull/tipped_arrow_pulling_shaft.png");

                                MockItemRenderer.renderTintedItem(poseStack, bufferSource, light, tipTex, color);
                                MockItemRenderer.renderItem(poseStack, bufferSource, light, shaftTex);

                            } else {
                                ResourceLocation arrowTex = archeryexp$getArrowTexture(arrow);

                                MockItemRenderer.renderItem(poseStack, bufferSource, light, arrowTex);
                            }

                            poseStack.popPose();
                        }
                    }
                }
            }
        }
    }

    // The weird name with the mod ID prefixed is to avoid collisions with other mods that have mixins which add methods of the same name.
    // This method was previously named "getArrowTexture".
    @Unique
    private ResourceLocation archeryexp$getArrowTexture(ItemStack arrow) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        ResourceLocation arrowResource = BuiltInRegistries.ITEM.getKey(arrow.getItem());

        ResourceLocation arrowTex = new ResourceLocation(arrowResource.getNamespace(),
                "textures/arrow_pull/" + arrowResource.getPath() + "_pulling.png");

        Optional<Resource> resource = resourceManager.getResource(arrowTex);

        if (resource.isPresent()) {
            return arrowTex;
        }
        return new ResourceLocation(ArcheryExpansion.MOD_ID, "textures/arrow_pull/iron_arrow_pulling.png");
    }
}
