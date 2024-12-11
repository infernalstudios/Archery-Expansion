package org.infernalstudios.archeryexp;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ArcheryExpansion {
    
    public ArcheryExpansion() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        Constants.LOGGER.info("Hello Forge world!");
        CommonClass.init();
        
    }
}