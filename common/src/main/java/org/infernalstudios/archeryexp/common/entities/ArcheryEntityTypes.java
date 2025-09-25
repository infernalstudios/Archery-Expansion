package org.infernalstudios.archeryexp.common.entities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.common.entities.arrow.DiamondArrow;
import org.infernalstudios.archeryexp.common.entities.arrow.GoldArrow;
import org.infernalstudios.archeryexp.common.entities.arrow.IronArrow;
import org.infernalstudios.archeryexp.common.entities.arrow.NetheriteArrow;
import org.infernalstudios.archeryexp.platform.Services;

import java.util.function.Supplier;

public class ArcheryEntityTypes {
    public static final Supplier<EntityType<IronArrow>> IRON_ARROW = registerArrow("iron", IronArrow::new);
    public static final Supplier<EntityType<GoldArrow>> GOLD_ARROW = registerArrow("gold", GoldArrow::new);
    public static final Supplier<EntityType<DiamondArrow>> DIAMOND_ARROW = registerArrow("diamond", DiamondArrow::new);
    public static final Supplier<EntityType<NetheriteArrow>> NETHERITE_ARROW = registerArrow("netherite", NetheriteArrow::new);

    public static <T extends AbstractArrow> Supplier<EntityType<T>> registerArrow(String type, EntityType.EntityFactory<T> factory) {
        return register(type + "_arrow", () -> EntityType.Builder.of(factory, MobCategory.MISC).sized(0.5f, 0.5f)
                        .build(ArcheryExpansion.MOD_ID + ":" + type + "_arrow"));
    }

    private static <T extends Entity> Supplier<EntityType<T>> register(String name, Supplier<EntityType<T>> entity) {
        return Services.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(ArcheryExpansion.MOD_ID, name), entity);
    }

    public static void register() {}
}
