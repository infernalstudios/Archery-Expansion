package org.infernalstudios.archeryexp.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;

public class ArrowHudThing {

    private static final ResourceLocation Bow_Meter = new ResourceLocation(ArcheryExpansion.MOD_ID, "textures/gui/bow_charge_meter.png");

    public static void renderBowBar(GuiGraphics drawContext, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;

        if (player == null) {
            return;
        }

        ItemStack stack = player.getUseItem();
        if (stack.getItem() instanceof BowItem) {

            int width = drawContext.guiWidth();
            int height = drawContext.guiHeight();

            int y = height / 2 - 16;
            int x = width / 2 - 8;

            RenderSystem.enableBlend();

//            RenderSystem.blendFuncSeparate(
//                    GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
//                    GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
//                    GlStateManager.SourceFactor.ONE,
//                    GlStateManager.DestFactor.ZERO
//            );

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.35f);

            drawContext.blit(Bow_Meter, x, y, 0, 0, 16, 7, 48, 7);

            BowProperties properties = (BowProperties) stack.getItem();

            float drawTime;
            if (properties.hasSpecialProperties())  {
                drawTime = BowUtil.getPowerForDrawTime(stack.getUseDuration() - player.getUseItemRemainingTicks(), properties);
            }
            else {
                drawTime = BowItem.getPowerForTime(player.getUseItem().getUseDuration() - player.getUseItemRemainingTicks());
            }

            if (drawTime >= 1) {
                drawContext.blit(Bow_Meter, x, y, 32, 0, 16, 7, 48, 7);
            } else {
                drawContext.blit(Bow_Meter, x, y, 16, 0, (int) (16 * drawTime), 7, 48, 7);
            }


            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    }

}
