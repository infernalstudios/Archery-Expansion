package org.infernalstudios.archeryexp.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.util.json.data.BowEffectData;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BowTooltipUtil {

    public static void addBowTooltips(List<Component> toolips, @NotNull ItemStack itemStack) {
        if (itemStack.getItem() instanceof IBowProperties bow && bow.archeryexp$isSpecial() && bow.archeryexp$hasDesc()) {
            boolean debug = Minecraft.getInstance().options.advancedItemTooltips;
            int i = Math.max(0, toolips.size() - (debug ? (itemStack.hasTag() ? 2 : 1) : 0));

            toolips.add(i++, CommonComponents.EMPTY);
            toolips.add(i++, Component.translatable("attribute.archeryexp.mainhand").withStyle(ChatFormatting.GRAY));
            toolips.add(i++, addAttributePos("base_damage", bow.archeryexp$getBaseDamage()));
            toolips.add(i++, addAttributePos("draw_cooldown", bow.archeryexp$getBowCooldown()));
            toolips.add(i++, addAttributePos("charge_time", bow.archeryexp$getChargeTime()));
            float speed = bow.archeryexp$getWalkSpeed();
            if (speed < 1) {
                toolips.add(i++, addAttributeNeg("walk_speed_down", round((speed - 1.0f) * -100) + "%"));
            } else if (speed > 1) {
                toolips.add(i++, addAttributePos("walk_speed_up", round((speed - 1.0f) * 100) + "%"));
            }
            float recoil = bow.archeryexp$getRecoil();
            if (recoil > 0) {
                toolips.add(i++, addAttributeNeg("recoil", round(bow.archeryexp$getRecoil() * 100)  + "%"));
            }

            toolips.add(i++, CommonComponents.EMPTY);
            appendStatusEffectTooltips(bow, toolips, i);
        }
    }

    private static float round(float f) {
        return Math.round(f * 100.0f) / 100.0f;
    }

    private static Component addAttributePos(String type, Object... args) {
        return CommonComponents.space().append(Component.translatable("attribute.archeryexp." + type, args)).withStyle(ChatFormatting.DARK_GREEN);
    }

    private static Component addAttributeNeg(String type, Object... args) {
        return CommonComponents.space().append(Component.translatable("attribute.archeryexp." + type, args)).withStyle(ChatFormatting.RED);
    }

    private static void appendStatusEffectTooltips(IBowProperties bow, List<Component> toolips, int i) {
        for (BowEffectData effect : bow.archeryexp$getEffects()) {
            MobEffect status = effect.getEffect();
            if (status == null) {
                status = effect.getFallbackEffect();
                if (status == null) {
                    continue;
                }
            }
            if (!effect.hasTooltip()) {
                continue;
            }

            MobEffectInstance mobEffectInstance = new MobEffectInstance(status, effect.getLength(), effect.getLevel());
            MutableComponent effectText = Component.translatable(mobEffectInstance.getDescriptionId());
            effectText = Component.translatable("potion.withAmplifier", effectText, Component.translatable("potion.potency." + mobEffectInstance.getAmplifier()));
            effectText = Component.translatable("potion.withDuration", effectText, MobEffectUtil.formatDuration(mobEffectInstance, 1));
            toolips.add(i++, effectText.withStyle(status.isBeneficial() ? ChatFormatting.BLUE : ChatFormatting.RED));
        }
    }
}
