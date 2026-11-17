package com.cinderstonestudios.simplyskillstweaks.config;

import com.stalemated.lib.compat.yacl.SLibTesting;
import com.stalemated.lib.config.InMemoryConfigManager;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class SimplySkillsTweaksConfigScreenTest {
    @Test
    void theConfigScreenBuilds() throws Exception {
        SLibTesting.assertScreenBuilds(
                SimplySkillsTweaksConfigScreen.builder(null, new InMemoryConfigManager<>(new SimplySkillsTweaksConfig(), SimplySkillsTweaksConfig::new, null)),
                Path.of(SimplySkillsTweaksConfigScreenTest.class.getResource("/assets/simply_skills_tweaks/lang/en_us.json").toURI())
        );
    }
}
