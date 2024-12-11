package org.infernalstudios.archeryexp.mixin;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    
//    @Inject(at = @At("HEAD"), method = "init()V")
//    private void init(CallbackInfo info) {
//
//        Constants.LOG.info("This line is printed by an example mod mixin from Fabric!");
//        Constants.LOG.info("MC Version: {}", Minecraft.getInstance().getVersionType());
//    }
}