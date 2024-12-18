package org.infernalstudios.archeryexp.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.ArcheryExpansion;

public class ArcheryTags {

    private static final TagKey<Item> CHAINMAIL = TagKey.create(Registries.ITEM, new ResourceLocation(ArcheryExpansion.MOD_ID, "projectile_protection_armor"));

}
