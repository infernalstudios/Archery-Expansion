package org.infernalstudios.archeryexp.common.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.ArcheryExpansion;

public class ArcheryTags {
    public static final TagKey<Item> NO_POWER = TagKey.create(Registries.ITEM, new ResourceLocation(ArcheryExpansion.MOD_ID, "anti_power_bow"));
    public static final TagKey<Item> CAN_BREAK_BOW = TagKey.create(Registries.ITEM, new ResourceLocation(ArcheryExpansion.MOD_ID, "breaks_bow"));
    public static final TagKey<EntityType<?>> ALLOW_HEADSHOT = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArcheryExpansion.MOD_ID, "headshot_whitelist"));
}
