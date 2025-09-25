package org.infernalstudios.archeryexp.client;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.common.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.util.ArcheryEnchantUtil;
import org.infernalstudios.archeryexp.util.BowUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class TrajectoryRenderer {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ArcheryExpansion.MOD_ID, "textures/trajectory_dot.png");

    public static void render(PoseStack poseStack, MultiBufferSource bufferSource, ClientLevel level) {
        level.players().forEach(user -> {
            ItemStack item = user.getUseItem();

            ArcheryEnchantUtil.enchantmentAction(ArcheryEnchants.TRAJECTORY, user, item, true, lvl -> {
                if (item.getItem() instanceof IBowProperties bow && user.isUsingItem()) {

                    BowUtil.getBowTrajectoryPoints(user, bow).forEach(vec3 -> {
                        poseStack.pushPose();

                        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
                        Vec3 cameraPos = camera.getPosition();

                        poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
                        poseStack.translate(vec3.x, vec3.y, vec3.z);
                        poseStack.mulPose(Axis.YN.rotationDegrees(camera.getYRot()));
                        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));

                        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
                        renderFace(poseStack, vertexConsumer);
                        poseStack.popPose();
                    });
                }
            });
        });
    }

    private static void renderFace(PoseStack poseStack, VertexConsumer vertexConsumer) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f m4f = pose.pose();
        Matrix3f m3f = pose.normal();

        int min = -1;
        int max = 1;

        vertex(vertexConsumer, m4f, m3f, max, min, 0, 1);
        vertex(vertexConsumer, m4f, m3f, min, min, 1, 1);
        vertex(vertexConsumer, m4f, m3f, min, max, 1, 0);
        vertex(vertexConsumer, m4f, m3f, max, max, 0, 0);

        vertex(vertexConsumer, m4f, m3f, min, max, 0, 1);
        vertex(vertexConsumer, m4f, m3f, max, max, 1, 1);
        vertex(vertexConsumer, m4f, m3f, max, min, 1, 0);
        vertex(vertexConsumer, m4f, m3f, min, min, 0, 0);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float u, float v) {
        vertexConsumer.vertex(matrix4f, x, y, 0).color(255, 255, 255, 25).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrix3f, 0, 1, 0).endVertex();
    }
}
