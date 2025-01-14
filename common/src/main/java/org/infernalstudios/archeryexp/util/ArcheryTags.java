package org.infernalstudios.archeryexp.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.ArcheryExpansion;

public class ArcheryTags {

    public static final TagKey<Item> DisallowPower = TagKey.create(Registries.ITEM, new ResourceLocation(ArcheryExpansion.MOD_ID, "anti_power_bow"));
    public static final TagKey<EntityType<?>> HeadshotWhitelist = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArcheryExpansion.MOD_ID, "headshot_whitelist"));

}
