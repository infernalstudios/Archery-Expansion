package org.infernalstudios.archeryexp.common.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.common.items.arrows.DiamondArrowItem;
import org.infernalstudios.archeryexp.common.items.arrows.GoldArrowItem;
import org.infernalstudios.archeryexp.common.items.arrows.IronArrowItem;
import org.infernalstudios.archeryexp.common.items.arrows.NetheriteArrowItem;
import org.infernalstudios.archeryexp.platform.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ArcheryItems {
    public static final List<Supplier<Item>> WEAPONS = new ArrayList<>();
    public static final List<Supplier<Item>> BOWS = new ArrayList<>();

    // Bows
    public static final Supplier<Item> GOLD_BOW = registerBow("gold", 80, Items.GOLD_INGOT);
    public static final Supplier<Item> IRON_BOW = registerBow("iron", 240, Items.IRON_INGOT);
    public static final Supplier<Item> DIAMOND_BOW = registerBow("diamond", 528, Items.DIAMOND);
    public static final Supplier<Item> NETHERITE_BOW = registerBow("netherite", 592, Items.NETHERITE_INGOT);
    public static final Supplier<Item> WOODEN_BOW = registerBow("wooden", 112, Items.STICK);

    // Arrows
    public static Supplier<Item> GOLD_ARROW = registerItem("gold_arrow", () -> new GoldArrowItem(new Item.Properties()));
    public static Supplier<Item> IRON_ARROW = registerItem("iron_arrow", () -> new IronArrowItem(new Item.Properties()));
    public static Supplier<Item> DIAMOND_ARROW = registerItem("diamond_arrow", () -> new DiamondArrowItem(new Item.Properties()));
    public static Supplier<Item> NETHERITE_ARROW = registerItem("netherite_arrow", () -> new NetheriteArrowItem(new Item.Properties().fireResistant()));

    private static Supplier<Item> registerBow(String type, int durability, Item repairMaterial) {
        Supplier<Item> regItem = registerItem(type + "_bow", () -> new ArcheryExpansionBow(new Item.Properties().stacksTo(1).durability(durability), repairMaterial));
        BOWS.add(regItem);
        return regItem;
    }

    private static Supplier<Item> registerItem(String name, Supplier<Item> item) {
        Supplier<Item> regItem = Services.PLATFORM.register(BuiltInRegistries.ITEM, new ResourceLocation(ArcheryExpansion.MOD_ID, name), item);
        WEAPONS.add(regItem);
        return regItem;
    }

    public static void register() {
        BOWS.add(() -> Items.BOW);
        ArcheryExpansion.LOGGER.debug("Registering Items for" + ArcheryExpansion.MOD_ID);
    }
}
