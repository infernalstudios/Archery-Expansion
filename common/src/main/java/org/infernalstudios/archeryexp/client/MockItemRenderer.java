package org.infernalstudios.archeryexp.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MockItemRenderer {
    public static class PixelData {
        public final boolean isOpaque;

        public PixelData(boolean isOpaque) {
            this.isOpaque = isOpaque;
        }
    }

    public static PixelData[][] loadPixelData(ResourceLocation texture, int alphaThreshold) {
        try (InputStream input = Minecraft.getInstance().getResourceManager().getResource(texture).get().open()
        ) {
            BufferedImage image = ImageIO.read(input);
            int width  = image.getWidth();
            int height = image.getHeight();

            PixelData[][] pixelData = new PixelData[width][height];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int argb  = image.getRGB(x, y);
                    int alpha = (argb >> 24) & 0xFF;

                    boolean isOpaque = (alpha >= alphaThreshold);
                    pixelData[x][y] = new PixelData(isOpaque);
                }
            }
            return pixelData;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + texture, e);
        }
    }

    public static void renderExtrudedSprite(PixelData[][] pixelData, float thickness, PoseStack poseStack,
                                            MultiBufferSource bufferSource, int light, ResourceLocation texture, int tint) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(texture));

        poseStack.pushPose();

        float halfZ = thickness * 0.5f;

        int width = pixelData.length;

        int height = (width > 0) ? pixelData[0].length : 0;

        Matrix4f pose = poseStack.last().pose();

        Matrix3f normal = poseStack.last().normal();

        int red = 255;
        int green = 255;
        int blue = 255;

        if (tint != -1) {
            red = (tint >> 16) & 0xFF;
            green = (tint >> 8) & 0xFF;
            blue = tint & 0xFF;
        }

        // Front face
        addQuad(
                buffer, pose, normal,
                0, 0, -halfZ,
                1, 0, -halfZ,
                1, 1, -halfZ,
                0, 1, -halfZ,
                computeUV(0, 0, 1, 1), computeUV(1, 0, 1, 1),
                computeUV(1, 1, 1, 1), computeUV(0, 1, 1, 1),
                light, 0, 0, 1, red, green, blue
        );

        // Back face
        addQuad(
                buffer, pose, normal,
                1, 0, halfZ,
                0, 0, halfZ,
                0, 1, halfZ,
                1, 1, halfZ,
                computeUV(1, 0, 1, 1), computeUV(0, 0, 1, 1),
                computeUV(0.0f, 1, 1, 1), computeUV(1, 1, 1, 1),
                light, 0, 0, -1, red, green, blue
        );

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                PixelData pd = pixelData[x][y];
                if (!pd.isOpaque) {
                    continue;
                }

                float scaledX = (float) x / width;
                float scaledY = (float) y / height;
                float scaledXNext = (float) (x + 1) / width;
                float scaledYNext = (float) (y + 1) / height;

                if (x == 0 || !pixelData[x - 1][y].isOpaque) {
                    float[][] sideUV = computeVerticalSliceUV(x, y, y + 1, width, height);
                    addQuad(
                            buffer, pose, normal,
                            scaledX, scaledY, halfZ,
                            scaledX, scaledY, -halfZ,
                            scaledX, scaledYNext, -halfZ,
                            scaledX, scaledYNext, halfZ,
                            sideUV[0], sideUV[1], sideUV[2], sideUV[3],
                            light, -1, 0, 0, red, green, blue
                    );
                }
                if (x == width - 1 || !pixelData[x + 1][y].isOpaque) {
                    float[][] sideUV = computeVerticalSliceUV(x, y, y + 1, width, height);
                    addQuad(
                            buffer, pose, normal,
                            scaledXNext, scaledY, -halfZ,
                            scaledXNext, scaledY, halfZ,
                            scaledXNext, scaledYNext, halfZ,
                            scaledXNext, scaledYNext, -halfZ,
                            sideUV[0], sideUV[1], sideUV[2], sideUV[3],
                            light, 1, 0, 0, red, green, blue
                    );
                }

                if (y == 0 || !pixelData[x][y - 1].isOpaque) {
                    float[][] sideUV = computeHorizontalSliceUV(x, x + 1, y, width, height);
                    addQuad(
                            buffer, pose, normal,
                            scaledX, scaledY, -halfZ,
                            scaledX, scaledY, halfZ,
                            scaledXNext, scaledY, halfZ,
                            scaledXNext, scaledY, -halfZ,
                            sideUV[0], sideUV[1], sideUV[2], sideUV[3],
                            light, 0, -1, 0, red, green, blue
                    );
                }
                if (y == height - 1 || !pixelData[x][y + 1].isOpaque) {
                    float[][] sideUV = computeHorizontalSliceUV(x, x + 1, y, width, height);
                    addQuad(
                            buffer, pose, normal,
                            scaledX, scaledYNext, halfZ,
                            scaledX, scaledYNext, -halfZ,
                            scaledXNext, scaledYNext, -halfZ,
                            scaledXNext, scaledYNext, halfZ,
                            sideUV[0], sideUV[1], sideUV[2], sideUV[3],
                            light, 0, 1, 0, red, green, blue
                    );
                }
            }
        }
        poseStack.popPose();
    }

    private static float[] computeUV(float x, float y, float width, float height) {
        float u = x / width;
        float v = y / height;
        return new float[]{u, v};
    }

    private static float[][] computeVerticalSliceUV(float x, float y0, float y1, float width, float height) {
        float u = x / width;
        float v0 = y0 / height;
        float v1 = y1 / height;

        return new float[][] {
                {u, v0},
                {u, v0},
                {u, v1},
                {u, v1}
        };
    }

    private static float[][] computeHorizontalSliceUV(float x0, float x1, float y, float width, float height) {
        float u0 = x0 / width;
        float u1 = x1 / width;
        float v = y / height;

        return new float[][] {
                {u0, v},
                {u1, v},
                {u1, v},
                {u0, v}
        };
    }

    private static void addQuad(VertexConsumer buffer, Matrix4f pose, Matrix3f normalMatrix,
                                float x0, float y0, float z0, float x1, float y1, float z1,
                                float x2, float y2, float z2, float x3, float y3, float z3,
                                float[] uv0, float[] uv1, float[] uv2, float[] uv3,
                                int light, float nx, float ny, float nz, int r, int g, int b) {

        buffer.vertex(pose, x0, y0, z0)
                .color(r, g, b, 255)
                .uv(uv0[0], uv0[1])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
        buffer.vertex(pose, x1, y1, z1)
                .color(r, g, b, 255)
                .uv(uv1[0], uv1[1])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
        buffer.vertex(pose, x2, y2, z2)
                .color(r, g, b, 255)
                .uv(uv2[0], uv2[1])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
        buffer.vertex(pose, x3, y3, z3)
                .color(r, g, b, 255)
                .uv(uv3[0], uv3[1])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
    }
}
