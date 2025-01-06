package org.infernalstudios.archeryexp;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArcheryExpansion {

    public static final String MOD_ID = "archeryexp";
    public static final String MOD_NAME = "Archery Expansion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        ArcheryEffects.registerEffects();
        ArcheryEnchants.registerEnchants();

        LOGGER.info("Finished Loading " + MOD_NAME);
    }
}