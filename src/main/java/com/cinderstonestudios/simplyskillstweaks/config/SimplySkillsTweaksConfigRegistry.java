package com.cinderstonestudios.simplyskillstweaks.config;

import com.cinderstonestudios.simplyskillstweaks.SimplySkillsTweaks;
import com.stalemated.lib.config.SLibConfig;
import com.stalemated.lib.config.SyncedConfigManager;

public final class SimplySkillsTweaksConfigRegistry {
    public static final SyncedConfigManager<SimplySkillsTweaksConfig> CONFIG = SLibConfig.synced(SimplySkillsTweaks.MOD_ID, SimplySkillsTweaksConfig.class).build();

    private SimplySkillsTweaksConfigRegistry() {
    }

    public static void register() {
        CONFIG.register();
    }
}
