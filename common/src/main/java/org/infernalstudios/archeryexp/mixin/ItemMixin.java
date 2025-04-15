package org.infernalstudios.archeryexp.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow public abstract boolean hasCraftingRemainingItem();

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    public void appendCustomAEBowText(ItemStack $$0, Level $$1, List<Component> $$2, TooltipFlag $$3, CallbackInfo ci) {

    }
}
