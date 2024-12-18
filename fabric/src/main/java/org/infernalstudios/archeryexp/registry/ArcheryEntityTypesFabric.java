package org.infernalstudios.archeryexp.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.entities.DiamondArrow;
import org.infernalstudios.archeryexp.entities.GoldArrow;
import org.infernalstudios.archeryexp.entities.IronArrow;

public class ArcheryEntityTypesFabric {
    public static void registerEntityTypes() {
        ArcheryEntityTypes.Iron_Arrow = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(ArcheryExpansion.MOD_ID, "iron_arrow"),
                FabricEntityTypeBuilder.create(MobCategory.CREATURE,  (EntityType<IronArrow> type, Level world) ->
                                new IronArrow(type, world))
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .build()
        );

        ArcheryEntityTypes.Gold_Arrow = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(ArcheryExpansion.MOD_ID, "gold_arrow"),
                FabricEntityTypeBuilder.create(MobCategory.CREATURE,  (EntityType<GoldArrow> type, Level world) ->
                                new GoldArrow(type, world))
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .build()
        );

        ArcheryEntityTypes.Diamond_Arrow = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(ArcheryExpansion.MOD_ID, "diamond_arrow"),
                FabricEntityTypeBuilder.create(MobCategory.CREATURE,  (EntityType<DiamondArrow> type, Level world) ->
                                new DiamondArrow(type, world))
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .build()
        );
    }
}
