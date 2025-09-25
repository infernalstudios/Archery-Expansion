package org.infernalstudios.archeryexp.common.enchants;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.platform.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ArcheryEnchants {

    public static final List<Supplier<Enchantment>> BOW_ENCHANTS = new ArrayList<>();

    // ENCHANTS

    public static final Supplier<Enchantment> SCOUTING = register("scouting", () ->
            new BaseEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET}, 3, true));

    public static final Supplier<Enchantment> BABY_FACE = register("baby_face", () ->
            new BaseEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET}, 2, true));

    public static final Supplier<Enchantment> FOLLOW_THROUGH = register("follow_through", () ->
            new BaseEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 2, true));

    public static final Supplier<Enchantment> GRIT = register("grit", () ->
            new BaseEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 2, true));

    public static final Supplier<Enchantment> SHATTERING = register("shattering", () ->
            new BaseEnchant(Enchantment.Rarity.COMMON, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}, 5));

    public static final Supplier<Enchantment> TRAJECTORY = register("trajectory", () ->
            new BaseEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}, 1, true));

    public static final Supplier<Enchantment> HEADSHOT = register("headshot", () ->
            new HeadshotEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}, 5));

    // CURSES

    public static final Supplier<Enchantment> FRAGILITY = register("fragility", () ->
            new BaseEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR,
                    new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}, 5, true, true));

    public static final Supplier<Enchantment> PINCUSHIONING = register("pincushioning", () ->
            new BaseEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD}, 5, true, true));


    private static Supplier<Enchantment> register(String name, Supplier<Enchantment> enchantment) {
        return Services.PLATFORM.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(ArcheryExpansion.MOD_ID, name), enchantment);
    }

    public static void register() {
        BOW_ENCHANTS.add(SHATTERING);
        BOW_ENCHANTS.add(HEADSHOT);
        BOW_ENCHANTS.add(() -> Enchantments.UNBREAKING);
    }
}
