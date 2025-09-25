package org.infernalstudios.archeryexp.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.client.MockItemRenderer;
import org.infernalstudios.archeryexp.client.TrajectoryRenderer;
import org.infernalstudios.archeryexp.util.ArrowPullUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V", shift = At.Shift.AFTER))
    public void archeryexp$render(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int $$6, BakedModel model, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;

        if (level != null) {
            for (Entity entity : level.entitiesForRendering()) {

                if (entity instanceof LivingEntity user && stack.getItem() instanceof IBowProperties bow && archeryexp$usingValid(bow, user, stack)) {

                    ItemStack arrow = user.getProjectile(stack);

                    if (!arrow.isEmpty()) {
                        float pull = BowUtil.getPowerForDrawTime(stack.getUseDuration() - user.getUseItemRemainingTicks(), bow);
                        float offset = pull >= 0.9f ? 0.125f : (pull >= 0.65f ? 0.0625f : 0);

                        poseStack.pushPose();

                        poseStack.scale(1.01f, 1.01f, 1.01f);
                        poseStack.translate(0.995f + offset - (0.125f * -bow.archeryexp$getOffsetX()), 0.995f - offset - (0.125f * bow.archeryexp$getOffsetY()), 0.495f);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

                        if (arrow.is(Items.TIPPED_ARROW)) {
                            MockItemRenderer.renderTintedItem(poseStack, bufferSource, light, ArrowPullUtil.TIPPED_TIP, PotionUtils.getColor(arrow));
                            MockItemRenderer.renderItem(poseStack, bufferSource, light, ArrowPullUtil.TIPPED_SHAFT);
                        } else {
                            MockItemRenderer.renderItem(poseStack, bufferSource, light, ArrowPullUtil.getPullingTexture(arrow));
                        }

                        poseStack.popPose();
                    }
                }
            }
        }
    }


    @Unique
    public boolean archeryexp$usingValid(IBowProperties bow, LivingEntity user, ItemStack stack) {
        return bow.archeryexp$isSpecial() && user.isUsingItem() && user.getUseItem() == stack;
    }
}
