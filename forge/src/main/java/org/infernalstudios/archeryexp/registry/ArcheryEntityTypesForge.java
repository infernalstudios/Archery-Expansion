package org.infernalstudios.archeryexp.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.entities.DiamondArrow;
import org.infernalstudios.archeryexp.entities.GoldArrow;
import org.infernalstudios.archeryexp.entities.IronArrow;

public class ArcheryEntityTypesForge {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ArcheryExpansion.MOD_ID);

    public static final RegistryObject<EntityType<IronArrow>> Iron_Arrow = ENTITY_TYPES.register(
            "iron_arrow",
            () -> EntityType.Builder.<IronArrow>of(IronArrow::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(ArcheryExpansion.MOD_ID + ":iron_arrow")
    );

    public static final RegistryObject<EntityType<GoldArrow>> Gold_Arrow = ENTITY_TYPES.register(
            "gold_arrow",
            () -> EntityType.Builder.<GoldArrow>of(GoldArrow::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(ArcheryExpansion.MOD_ID + ":gold_arrow")
    );

    public static final RegistryObject<EntityType<DiamondArrow>> Diamond_Arrow = ENTITY_TYPES.register(
            "diamond_arrow",
            () -> EntityType.Builder.<DiamondArrow>of(DiamondArrow::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(ArcheryExpansion.MOD_ID + ":diamond_arrow")
    );

    public static void registerEntities(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }

    public static void registerEntitiesCommon() {
        ArcheryEntityTypes.Iron_Arrow = ArcheryEntityTypesForge.Iron_Arrow.get();
        ArcheryEntityTypes.Gold_Arrow = ArcheryEntityTypesForge.Gold_Arrow.get();
        ArcheryEntityTypes.Diamond_Arrow = ArcheryEntityTypesForge.Diamond_Arrow.get();
    }
}
