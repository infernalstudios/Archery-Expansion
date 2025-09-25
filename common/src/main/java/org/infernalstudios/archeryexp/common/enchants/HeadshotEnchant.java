package org.infernalstudios.archeryexp.common.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class HeadshotEnchant extends BaseEnchant {
    protected HeadshotEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLvl) {
        super(rarity, category, slots, maxLvl);
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != Enchantments.POWER_ARROWS;
    }
}
