package org.infernalstudios.archeryexp.mixin.enchant;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @ModifyReturnValue(method = "canEnchant", at = @At(value = "RETURN"))
    protected boolean archeryexp$canEnchant(boolean original, ItemStack itemStack) {
        return original;
    }
}
