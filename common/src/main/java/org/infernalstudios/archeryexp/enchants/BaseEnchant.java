package org.infernalstudios.archeryexp.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BaseEnchant extends Enchantment {

    private final boolean curse;
    private final int maxLvl;

    protected BaseEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLvl) {
        this(rarity, category, slots, maxLvl, false);
    }

    protected BaseEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLvl, boolean curse) {
        super(rarity, category, slots);
        this.maxLvl = maxLvl;
        this.curse = curse;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLvl;
    }

    @Override
    public boolean isCurse() {
        return this.curse;
    }

    @Override
    public boolean isTreasureOnly() {
        return this.curse;
    }
}
