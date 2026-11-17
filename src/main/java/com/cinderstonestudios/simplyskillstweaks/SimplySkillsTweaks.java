package com.cinderstonestudios.simplyskillstweaks;

import com.cinderstonestudios.simplyskillstweaks.config.SimplySkillsTweaksConfigRegistry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SimplySkillsTweaks implements ModInitializer {
    public static final String MOD_ID = "simply_skills_tweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        SimplySkillsTweaksConfigRegistry.register();
    }
}
