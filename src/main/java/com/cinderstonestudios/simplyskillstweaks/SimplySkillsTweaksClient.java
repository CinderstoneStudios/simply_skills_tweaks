package com.cinderstonestudios.simplyskillstweaks;

import com.cinderstonestudios.simplyskillstweaks.config.SimplySkillsTweaksConfigScreen;
import com.stalemated.lib.client.SLibConfigScreens;
import net.fabricmc.api.ClientModInitializer;

public final class SimplySkillsTweaksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SLibConfigScreens.register(SimplySkillsTweaks.MOD_ID, () -> SimplySkillsTweaksConfigScreen::create);
    }
}
