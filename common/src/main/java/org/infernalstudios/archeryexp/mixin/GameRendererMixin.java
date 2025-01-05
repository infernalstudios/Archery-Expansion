package org.infernalstudios.archeryexp.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.PlayerFOV;
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

    @Shadow private float fov;
    @Shadow @Final private Minecraft minecraft;
    @Shadow private float oldFov;
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
            PlayerFOV fovPlayer = (PlayerFOV) player;
            double fov = fovPlayer.getPlayerFOVWithoutBow();
            if (speedAttribute != null && speedAttribute.getModifier(ArcheryExpansion.BOW_DRAW_SPEED_MODIFIER_ID) != null) {
                this.timer = 40;
                cir.setReturnValue(fov);
            }
            else if (timer > 0) {
                timer--;
                cir.setReturnValue(fov);
            }
            else {
                double currentFOV = (double) this.minecraft.options.fov().get().intValue();
                currentFOV *= (double) Mth.lerp(partialTicks, this.oldFov, this.fov);
                fovPlayer.setPlayerFOVWithoutBow(currentFOV);
            }
        }
    }
}
