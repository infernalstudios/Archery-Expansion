package org.infernalstudios.archeryexp.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BaseEnchant extends Enchantment {

    private final boolean curse;
    private final int maxLvl;

    protected BaseEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLvl) {
        super(rarity, category, slots);
        this.maxLvl = maxLvl;
        this.curse = false;
    }

    protected BaseEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLvl, boolean curse) {
        super(rarity, category, slots);
        this.maxLvl = maxLvl;
        this.curse = curse;
    }

    @Override
    public int getMaxLevel() {
        return super.getMaxLevel();
    }

    @Override
    public boolean isCurse() {
        return super.isCurse();
    }
}
