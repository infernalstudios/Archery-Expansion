package org.infernalstudios.archeryexp.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ArrowDamageEnchantment;
import org.infernalstudios.archeryexp.util.ArcheryTags;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArrowDamageEnchantment.class)
public class ArrowDamageEnchantmentMixin extends EnchantmentMixin {

    @Override
    protected boolean powerEnchant(boolean original, ItemStack itemStack) {
        return original && !itemStack.is(ArcheryTags.DisallowPower);
    }

}