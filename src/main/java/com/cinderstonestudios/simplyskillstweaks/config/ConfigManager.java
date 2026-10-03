package com.cinderstonestudios.simplyskillstweaks.config;

import com.stalemated.lib.config.SLibConfig;
import com.stalemated.lib.config.manager.SyncedConfigManager;

import static com.cinderstonestudios.simplyskillstweaks.SimplySkillsTweaks.LOGGER;
import static com.cinderstonestudios.simplyskillstweaks.SimplySkillsTweaks.MOD_ID;

public class ConfigManager {

    public static final SyncedConfigManager<SSTConfig> MANAGER = SLibConfig.syncedBuilder(SSTConfig.class)
            .modId(MOD_ID)
            .logger(LOGGER)
            .register();

    public static void init() {
    }

    public static SSTConfig getConfig() {
        return MANAGER.getActiveConfig();
    }
}
