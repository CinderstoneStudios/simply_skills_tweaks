package com.cinderstonestudios.simplyskillstweaks.config;

import com.stalemated.lib.config.annotation.Comment;
import com.stalemated.lib.config.annotation.RangeFloat;

public class SSTConfig {
    @Comment("""
            Toggles the new Righteous Hammers formula.

            Formula breakdown:
              Final Damage = max(Physical Branch, Tank Branch, Holy/Lightning Branch)

              1. Physical Branch (with anti-exploit soft-cap):
                 If Attack Damage <= physicalSoftCapThreshold:
                    Damage = Attack Damage * physicalAttackMultiplier
                 Else:
                    Damage = (physicalSoftCapThreshold * physicalAttackMultiplier)
                           + ((Attack Damage - physicalSoftCapThreshold) * physicalExcessMultiplier)

              2. Defensive Tank Branch:
                 Base = (Attack Damage * tankAttackMultiplier)
                      + (Max Health * tankHealthMultiplier)
                      + (Armor * tankArmorMultiplier)
                 Damage = Base * (isHoldingShield ? shieldBonusMultiplier : 1.0)

              3. Holy / Lightning Branch:
                 Elemental Power = max(Healing Spell Power, Lightning Spell Power)
                 Damage = (Attack Damage * holyLightningAttackMultiplier)
                        + (Elemental Power * holyLightningSpellMultiplier)
                        + (Armor * holyLightningArmorMultiplier)
            """)
    public boolean toggleNewHammerFormula = true;

    @Comment("Enables debug logging")
    public boolean debugLogging = true;

    // Physical Branch
    @Comment("Physical attack damage multiplier up to the soft-cap threshold (Default: 0.50, Vanilla was 0.80)")
    @RangeFloat(min = 0.0f, max = 2.0f)
    public float physicalAttackMultiplier = 0.50f;

    @Comment("Attack damage threshold where physical soft-cap begins (Default: 120.0)")
    @RangeFloat(min = 20.0f, max = 500.0f)
    public float physicalSoftCapThreshold = 120.0f;

    @Comment("Diminishing multiplier for attack damage exceeding the soft-cap threshold (Default: 0.20)")
    @RangeFloat(min = 0.0f, max = 1.0f)
    public float physicalExcessMultiplier = 0.20f;

    // Tank Branch
    @Comment("Residual attack damage multiplier for tanks (Default: 0.05)")
    @RangeFloat(min = 0.0f, max = 1.0f)
    public float tankAttackMultiplier = 0.05f;

    @Comment("Max Health multiplier for tanks (Default: 0.015)")
    @RangeFloat(min = 0.0f, max = 0.10f)
    public float tankHealthMultiplier = 0.015f;

    @Comment("Armor multiplier for tanks (Default: 0.30)")
    @RangeFloat(min = 0.0f, max = 2.0f)
    public float tankArmorMultiplier = 0.30f;

    @Comment("Bonus damage multiplier when holding a Shield in off-hand (Default: 1.35 = +35%)")
    @RangeFloat(min = 1.0f, max = 3.0f)
    public float shieldBonusMultiplier = 1.35f;

    // Holy / Lightning Branch
    @Comment("Attack damage multiplier for Holy/Lightning hybrid builds (Default: 0.0)")
    @RangeFloat(min = 0.0f, max = 1.0f)
    public float holyLightningAttackMultiplier = 0.0f;

    @Comment("Spell power multiplier (max of Healing and Lightning power) (Default: 0.60)")
    @RangeFloat(min = 0.0f, max = 3.0f)
    public float holyLightningSpellMultiplier = 0.60f;

    @Comment("Armor multiplier for Holy/Lightning builds (Default: 0.20)")
    @RangeFloat(min = 0.0f, max = 2.0f)
    public float holyLightningArmorMultiplier = 0.20f;
}
