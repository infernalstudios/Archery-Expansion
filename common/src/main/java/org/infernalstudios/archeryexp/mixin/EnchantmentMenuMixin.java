package org.infernalstudios.archeryexp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ArrowDamageEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.items.ArcheryExpansionBow;
import org.infernalstudios.archeryexp.util.ArcheryTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {

    @WrapOperation(
            method = "getEnchantmentList",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;IZ)Ljava/util/List;"
            )
    )
    private List<EnchantmentInstance> preventPowerEnchTable(RandomSource rand, ItemStack stack, int $$2, boolean $$3, Operation<List<EnchantmentInstance>> original) {
        List<EnchantmentInstance> origin = original.call(rand, stack, $$2, $$3);

        if (stack.is(ArcheryTags.DisallowPower)) {
            origin.replaceAll(i -> i.enchantment instanceof ArrowDamageEnchantment ? new EnchantmentInstance(ArcheryEnchants.Bow_Enchants.get(rand.nextInt(2)), Math.min(i.level, 3)) : i);
        }

        return origin;
    }

}
