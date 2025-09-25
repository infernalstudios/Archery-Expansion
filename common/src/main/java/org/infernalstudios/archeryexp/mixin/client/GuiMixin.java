package org.infernalstudios.archeryexp.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.infernalstudios.archeryexp.util.BowUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    // Why is this a mixin rather than a hud render event you ask?
    // This is because Forge is an asshole and won't let me apply the negative render thing there.
    // Fabric worked just fine, but Forge? Noooo!

    @Unique
    private static final ResourceLocation BOW_METER = new ResourceLocation(ArcheryExpansion.MOD_ID, "textures/gui/bow_charge_meter.png");

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V"))
    private void archeryexp$renderCrosshair(GuiGraphics drawContext, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            ItemStack stack = player.getUseItem();
            if (stack.getItem() instanceof BowItem) {
                int width = drawContext.guiWidth();
                int height = drawContext.guiHeight();

                int y = height / 2 - 16;
                int x = width / 2 - 8;

                drawContext.blit(BOW_METER, x, y, 0, 0, 16, 7, 48, 7);

                IBowProperties properties = (IBowProperties) stack.getItem();

                float drawTime;
                if (properties.archeryexp$isSpecial())  {
                    drawTime = BowUtil.getPowerForDrawTime(stack.getUseDuration() - player.getUseItemRemainingTicks(), properties);
                }
                else {
                    drawTime = BowItem.getPowerForTime(player.getUseItem().getUseDuration() - player.getUseItemRemainingTicks());
                }

                if (drawTime >= 1) {
                    drawContext.blit(BOW_METER, x, y, 32, 0, 16, 7, 48, 7);
                } else {
                    drawContext.blit(BOW_METER, x, y, 16, 0, (int) (16 * drawTime), 7, 48, 7);
                }
            }
        }
    }
}
