package org.infernalstudios.archeryexp.enchants;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.infernalstudios.archeryexp.effects.QuickdrawEffect;
import org.infernalstudios.archeryexp.platform.Services;

import java.util.ArrayList;
import java.util.List;

public class ArcheryEnchants {

    public static final List<Enchantment> Bow_Enchants = new ArrayList<>();

    public static final Enchantment SCOUTING = new BaseEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET}, 3, true);
    public static final Enchantment BABY_FACE = new BaseEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET}, 2, true);
    public static final Enchantment FOLLOW_THROUGH = new BaseEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 2, true);
    public static final Enchantment GRIT = new BaseEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 2, true);
    public static final Enchantment SHATTERING = new BaseEnchant(Enchantment.Rarity.COMMON, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}, 5);
    public static final Enchantment TRAJECTORY = new BaseEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}, 1, true);
    public static final Enchantment HEADSHOT = new HeadshotEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}, 5);


    public static final Enchantment FRAGILITY = new BaseEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR,
            new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}, 5, true, true);
    public static final Enchantment PINCUSHIONING = new BaseEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD}, 5, true, true);

    public static void registerEnchants() {
        register("scouting", SCOUTING);
        register("baby_face", BABY_FACE);
        register("follow_through", FOLLOW_THROUGH);
        register("grit", GRIT);
        register("shattering", SHATTERING);
        register("trajectory", TRAJECTORY);
        register("headshot", HEADSHOT);


        register("fragility", FRAGILITY);
        register("pincushioning", PINCUSHIONING);

        Bow_Enchants.add(SHATTERING);
        Bow_Enchants.add(HEADSHOT);
        Bow_Enchants.add(Enchantments.UNBREAKING);
    }

    private static void register(String name, Enchantment enchant) {
        Services.PLATFORM.registerEnchantment(name, enchant);
    }
}
