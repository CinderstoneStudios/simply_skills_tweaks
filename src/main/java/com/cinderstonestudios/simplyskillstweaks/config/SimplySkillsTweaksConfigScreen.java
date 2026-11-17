package com.cinderstonestudios.simplyskillstweaks.config;

import com.cinderstonestudios.simplyskillstweaks.SimplySkillsTweaks;
import com.stalemated.lib.compat.yacl.builder.YaclCategoryBuilder;
import com.stalemated.lib.compat.yacl.builder.YaclScreenBuilder;
import com.stalemated.lib.config.ConfigManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SimplySkillsTweaksConfigScreen {
    private SimplySkillsTweaksConfigScreen() {
    }

    public static Screen create(Screen parent) {
        return builder(parent, SimplySkillsTweaksConfigRegistry.CONFIG).generateScreen();
    }

    static YaclScreenBuilder builder(Screen parent, ConfigManager<SimplySkillsTweaksConfig> config) {
        return YaclScreenBuilder.create(parent, Component.translatable("simply_skills_tweaks.config_screen.title"), SimplySkillsTweaks.MOD_ID)
                .category(config, "righteous_hammers", YaclCategoryBuilder::autoGenerate);
    }
}
