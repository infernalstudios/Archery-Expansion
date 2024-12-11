package org.infernalstudios.archeryexp;

import net.fabricmc.api.ModInitializer;

public class ArcheryExpansionFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ArcheryExpansion.init();
    }
}
