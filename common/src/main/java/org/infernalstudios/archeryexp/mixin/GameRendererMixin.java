package org.infernalstudios.archeryexp.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Unique
    private int timer = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(Minecraft $$0, ItemInHandRenderer $$1, ResourceManager $$2, RenderBuffers $$3, CallbackInfo ci) {
        this.timer = 0;
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(Camera camera, float partialTicks, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCameraEntity() instanceof Player player) {
            AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
            double baseFov = mc.options.fov().get();
            if (speedAttribute != null && speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID) != null) {
                this.timer = 5;
                cir.setReturnValue(baseFov);
            }
            else if (timer > 0) {
                cir.setReturnValue(baseFov);
            }
        }
    }
}
