package org.infernalstudios.archeryexp.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.function.Supplier;

public class ArcheryEnchantUtil {

    /**
     * Applies a status effect for an enchantment (which many AE enchants do)
     * @param enchant the supplier for the enchantment (is a supplier only because all AE enchants are suppliers, so less typing when calling the method)
     * @param entity the entity who the enchantment is checked on
     * @param stack the stack that the enchantment could be on
     * @param bl extra conditions for triggering enchantment action
     * @param effect the status effect
     * @param duration the duration of the effect
     */
    public static void effectEnchantmentAction(Supplier<Enchantment> enchant, LivingEntity entity, ItemStack stack, boolean bl, MobEffect effect, int duration) {
        effectEnchantmentAction(enchant, entity, stack, bl, effect, duration, false);
    }

    /**
     * Applies a status effect for an enchantment (which many AE enchants do)
     * @param enchant the supplier for the enchantment (is a supplier only because all AE enchants are suppliers, so less typing when calling the method)
     * @param entity the entity who the enchantment is checked on
     * @param stack the stack that the enchantment could be on
     * @param bl extra conditions for triggering enchantment action
     * @param effect the status effect
     * @param duration the duration of the effect
     * @param multiplier if true, multiply the duration by the level
     */
    public static void effectEnchantmentAction(Supplier<Enchantment> enchant, LivingEntity entity, ItemStack stack, boolean bl, MobEffect effect, int duration, boolean multiplier) {
        enchantmentAction(enchant, entity, stack, bl, (lvl) -> entity.addEffect(
                new MobEffectInstance(effect, multiplier ? duration * lvl : duration, lvl - 1, false, false))
        );
    }

    /**
     * Checks an Entities Item and Armor slots for an enchantment, and performs an action if it's present and bl is true
     * @param enchant the supplier for the enchantment (is a supplier only because all AE enchants are suppliers, so less typing when calling the method)
     * @param entity the entity who the enchantment is checked on
     * @param stack the stack that the enchantment could be on
     * @param bl extra conditions for triggering enchantment action
     * @param enchantAction a functional interface for the action
     */
    public static void enchantmentAction(Supplier<Enchantment> enchant, LivingEntity entity, ItemStack stack, boolean bl, EnchantAction enchantAction) {
        int itemLvl = EnchantmentHelper.getItemEnchantmentLevel(enchant.get(), stack);
        int armorLvl = EnchantmentHelper.getEnchantmentLevel(enchant.get(), entity);
        if ((armorLvl > 0 || itemLvl > 0) && bl) {
            enchantAction.action(itemLvl);
        }
    }


    /**
     * Functional Interface used for performing an action
     */
    @FunctionalInterface
    public interface EnchantAction {
        void action(int lvl);
    }
}
