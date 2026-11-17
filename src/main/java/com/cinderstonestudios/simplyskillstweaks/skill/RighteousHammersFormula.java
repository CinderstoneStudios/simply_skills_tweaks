package com.cinderstonestudios.simplyskillstweaks.skill;

import com.cinderstonestudios.simplyskillstweaks.config.SimplySkillsTweaksConfig;

/**
 * Class responsible for calculating the balanced damage scaling for the Righteous Hammers ability.
 * Supports three distinct archetypes via mutual exclusivity (max):
 * <ul>
 *  <li>Physical DPS (Warrior / Rogue, with soft-capped scaling)</li>
 *  <li>Tank (Pure Shield Tank / Blood Death Knight)</li>
 *  <li>Holy / Lightning Hybrid (Paladin / Valkyrie)</li>
 * </ul>
 */
public final class RighteousHammersFormula {
    private RighteousHammersFormula() {
    }

    public static HammerBranchDamage calculate(SimplySkillsTweaksConfig config, CombatStats stats) {
        return new HammerBranchDamage(physical(config, stats), tank(config, stats), holyLightning(config, stats));
    }

    private static float physical(SimplySkillsTweaksConfig config, CombatStats stats) {
        float softCap = config.physicalSoftCapThreshold;
        if (stats.attackDamage() <= softCap) return stats.attackDamage() * config.physicalAttackMultiplier;
        return (softCap * config.physicalAttackMultiplier)
                + ((stats.attackDamage() - softCap) * config.physicalExcessMultiplier);
    }

    private static float tank(SimplySkillsTweaksConfig config, CombatStats stats) {
        float base = (stats.attackDamage() * config.tankAttackMultiplier)
                + (stats.maxHealth() * config.tankHealthMultiplier)
                + (stats.armor() * config.tankArmorMultiplier);
        return stats.holdingShield() ? base * config.shieldBonusMultiplier : base;
    }

    private static float holyLightning(SimplySkillsTweaksConfig config, CombatStats stats) {
        return (stats.attackDamage() * config.holyLightningAttackMultiplier)
                + (stats.spellPower() * config.holyLightningSpellMultiplier)
                + (stats.armor() * config.holyLightningArmorMultiplier);
    }
}
