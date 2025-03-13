package org.infernalstudios.archeryexp.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArcheryExpansionBow extends BowItem {

    private final Item repairItem;

    public ArcheryExpansionBow(Properties properties, Item repairItem) {
        super(properties);
        this.repairItem = repairItem;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairStack.is(this.repairItem);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, world, tooltip, tooltipFlag);
        tooltip.add(Component.literal(" "));
        tooltip.add(Component.translatable("attribute.archeryexp.mainhand").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(" " + getBaseDamage() + " ")
                .append(Component.translatable("attribute.archeryexp.base_damage")).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GREEN)));
        tooltip.add(Component.literal(" " + getBowCooldown() + " ")
                .append(Component.translatable("attribute.archeryexp.draw_cooldown")).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GREEN)));
    }

    private float getBowCooldown() {
        return getBowProperties().getBowCooldown();
    }

    private float getBaseDamage() {
        return getBowProperties().getBaseDamage();
    }

    private BowProperties getBowProperties() {
        return ((BowProperties) this);
    }


}
