package org.infernalstudios.archeryexp;

import net.minecraft.server.level.ServerPlayer;
import org.infernalstudios.archeryexp.client.particles.ArcheryParticles;
import org.infernalstudios.archeryexp.common.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.common.enchants.ArcheryEnchants;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ArcheryExpansion {

    // Networking stuff is still split by Client and Server, I don't think it'd be worth my time to do anything ab that

    public static final String MOD_ID = "archeryexp";
    public static final String MOD_NAME = "Archery Expansion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final Set<ServerPlayer> BOW_STAT_PLAYER_LIST = new HashSet<>();
    public static final UUID BOW_DRAW_SPEED_MODIFIER_ID = UUID.fromString("f7724e94-06e7-4214-8986-52c9a245e792");

    public static void init() {
        ArcheryItems.register();
        ArcheryEntityTypes.register();
        ArcheryEffects.register();
        ArcheryEnchants.register();
        ArcheryParticles.register();
        LOGGER.info("Finished Loading " + MOD_NAME);
    }
}