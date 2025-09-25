package org.infernalstudios.archeryexp.mixin.enchant;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ArrowDamageEnchantment;
import org.infernalstudios.archeryexp.common.misc.ArcheryTags;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArrowDamageEnchantment.class)
public class ArrowDamageEnchantmentMixin extends EnchantmentMixin {

    @Override
    protected boolean archeryexp$canEnchant(boolean original, ItemStack itemStack) {
        return original && !itemStack.is(ArcheryTags.NO_POWER);
    }

}