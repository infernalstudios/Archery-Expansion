package org.infernalstudios.archeryexp.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ArrowDamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import org.infernalstudios.archeryexp.items.ArcheryExpansionBow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @ModifyReturnValue(method = "canEnchant", at = @At(value = "TAIL"))
    protected boolean powerEnchant(boolean original, ItemStack itemStack) {
        return original;
    }

    @Mixin(ArrowDamageEnchantment.class)
    public static class ArrowDamageEnchantmentMixin extends EnchantmentMixin {

        @Override
        protected boolean powerEnchant(boolean original, ItemStack itemStack) {
            return super.powerEnchant(original, itemStack) && !(itemStack.getItem() instanceof ArcheryExpansionBow);
        }
    }
}
