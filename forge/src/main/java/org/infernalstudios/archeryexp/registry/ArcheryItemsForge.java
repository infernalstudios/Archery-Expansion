package org.infernalstudios.archeryexp.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.items.ArcheryExpansionBow;
import org.infernalstudios.archeryexp.items.ArcheryItems;
import org.infernalstudios.archeryexp.items.arrows.DiamondArrowItem;
import org.infernalstudios.archeryexp.items.arrows.GoldArrowItem;
import org.infernalstudios.archeryexp.items.arrows.IronArrowItem;
import org.infernalstudios.archeryexp.items.arrows.NetheriteArrowItem;

@Mod.EventBusSubscriber(modid = ArcheryExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArcheryItemsForge {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ArcheryExpansion.MOD_ID);

    // Bows

    // Wood = 384

    public static final RegistryObject<Item> Gold_Bow = ITEMS.register("gold_bow",
            () -> new ArcheryExpansionBow(new Item.Properties().stacksTo(1).durability(80), Items.GOLD_INGOT));

    public static final RegistryObject<Item> Iron_Bow = ITEMS.register("iron_bow",
            () -> new ArcheryExpansionBow(new Item.Properties().stacksTo(1).durability(240), Items.IRON_INGOT));

    public static final RegistryObject<Item> Diamond_Bow = ITEMS.register("diamond_bow",
            () -> new ArcheryExpansionBow(new Item.Properties().stacksTo(1).durability(528), Items.DIAMOND));

    public static final RegistryObject<Item> Netherite_Bow = ITEMS.register("netherite_bow",
            () -> new ArcheryExpansionBow(new Item.Properties().stacksTo(1).durability(592).fireResistant(), Items.NETHERITE_INGOT));

    public static final RegistryObject<Item> Wooden_Bow = ITEMS.register("wooden_bow",
            () -> new ArcheryExpansionBow(new Item.Properties().stacksTo(1).durability(112), Items.STICK));

    // Arrows

    public static final RegistryObject<Item> Gold_Arrow = ITEMS.register("gold_arrow",
            () -> new GoldArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> Iron_Arrow = ITEMS.register("iron_arrow",
            () -> new IronArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> Diamond_Arrow = ITEMS.register("diamond_arrow",
            () -> new DiamondArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> Netherite_Arrow = ITEMS.register("netherite_arrow",
            () -> new NetheriteArrowItem(new Item.Properties()));



    public static void registerItems(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static void registerItemsCommon() {
        ArcheryItems.Gold_Bow = Gold_Bow.get();
        ArcheryItems.Iron_Bow = Iron_Bow.get();
        ArcheryItems.Diamond_Bow = Diamond_Bow.get();
        ArcheryItems.Netherite_Bow = Netherite_Bow.get();
        ArcheryItems.Wooden_Bow = Wooden_Bow.get();

        ArcheryItems.Gold_Arrow = Gold_Arrow.get();
        ArcheryItems.Iron_Arrow = Iron_Arrow.get();
        ArcheryItems.Diamond_Arrow = Diamond_Arrow.get();
        ArcheryItems.Netherite_Arrow = Netherite_Arrow.get();
    }

    @SubscribeEvent
    public static void addItemsToCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(Gold_Bow.get());
            event.accept(Iron_Bow.get());
            event.accept(Diamond_Bow.get());
            event.accept(Netherite_Bow.get());
            event.accept(Wooden_Bow.get());

            event.accept(Gold_Arrow.get());
            event.accept(Iron_Arrow.get());
            event.accept(Diamond_Arrow.get());
            event.accept(Netherite_Arrow.get());
        }
    }
}

