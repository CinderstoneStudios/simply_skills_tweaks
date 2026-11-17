package com.cinderstonestudios.simplyskillstweaks.skill;

import com.cinderstonestudios.simplyskillstweaks.config.SimplySkillsTweaksConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RighteousHammersFormulaTest {
    private static final float TOLERANCE = 1e-4f;

    private final SimplySkillsTweaksConfig config = new SimplySkillsTweaksConfig();

    @Test
    void highAttackDamageWinsWithThePhysicalBranch() {
        HammerBranchDamage damage = RighteousHammersFormula.calculate(config, new CombatStats(40, 20, 0, 0, false));

        assertEquals(HammerBranch.PHYSICAL, damage.winner());
        assertEquals(20f, damage.amount(), TOLERANCE);
    }

    @Test
    void highHealthAndArmorWinWithTheTankBranch() {
        HammerBranchDamage damage = RighteousHammersFormula.calculate(config, new CombatStats(2, 400, 30, 0, false));

        assertEquals(HammerBranch.TANK, damage.winner());
        assertEquals(15.1f, damage.amount(), TOLERANCE);
    }

    @Test
    void highSpellPowerWinsWithTheHolyLightningBranch() {
        HammerBranchDamage damage = RighteousHammersFormula.calculate(config, new CombatStats(2, 20, 10, 50, false));

        assertEquals(HammerBranch.HOLY_LIGHTNING, damage.winner());
        assertEquals(32f, damage.amount(), TOLERANCE);
    }

    @Test
    void attackDamageBelowTheSoftCapScalesFully() {
        assertEquals(50f, physicalDamage(100), TOLERANCE);
    }

    @Test
    void attackDamageAtTheSoftCapScalesFully() {
        assertEquals(60f, physicalDamage(120), TOLERANCE);
    }

    @Test
    void attackDamageAboveTheSoftCapScalesOnlyTheExcessByTheExcessMultiplier() {
        assertEquals(80f, physicalDamage(220), TOLERANCE);
    }

    @Test
    void aShieldInTheOffhandMultipliesTheTankBranch() {
        float withoutShield = RighteousHammersFormula.calculate(config, new CombatStats(2, 400, 30, 0, false)).tank();
        float withShield = RighteousHammersFormula.calculate(config, new CombatStats(2, 400, 30, 0, true)).tank();

        assertEquals(withoutShield * 1.35f, withShield, TOLERANCE);
    }

    @Test
    void tiesGoToPhysicalFirstAndTankSecond() {
        config.physicalAttackMultiplier = 0.5f;
        config.tankAttackMultiplier = 0.5f;
        config.tankHealthMultiplier = 0f;
        config.tankArmorMultiplier = 0f;
        config.holyLightningArmorMultiplier = 0f;
        assertEquals(HammerBranch.PHYSICAL, RighteousHammersFormula.calculate(config, new CombatStats(10, 20, 10, 0, false)).winner());

        config.physicalAttackMultiplier = 0f;
        config.tankAttackMultiplier = 0f;
        config.tankArmorMultiplier = 0.3f;
        config.holyLightningArmorMultiplier = 0.3f;
        assertEquals(HammerBranch.TANK, RighteousHammersFormula.calculate(config, new CombatStats(10, 20, 10, 0, false)).winner());
    }

    private float physicalDamage(float attackDamage) {
        return RighteousHammersFormula.calculate(config, new CombatStats(attackDamage, 20, 0, 0, false)).physical();
    }
}
