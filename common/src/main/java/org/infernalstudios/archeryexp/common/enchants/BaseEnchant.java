package org.infernalstudios.archeryexp.common.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BaseEnchant extends Enchantment {

    private final boolean curse;
    private final boolean treasure;
    private final int maxLvl;

    protected BaseEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLvl) {
        this(rarity, category, slots, maxLvl, false, false);
    }

    protected BaseEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLvl, boolean treasure) {
        this(rarity, category, slots, maxLvl, false, treasure);
    }

    protected BaseEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, int maxLvl, boolean curse, boolean treasure) {
        super(rarity, category, slots);
        this.maxLvl = maxLvl;
        this.curse = curse;
        this.treasure = treasure;
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
        return this.curse || this.treasure;
    }
}
