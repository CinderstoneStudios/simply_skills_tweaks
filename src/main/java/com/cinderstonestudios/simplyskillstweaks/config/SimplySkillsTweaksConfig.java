package com.cinderstonestudios.simplyskillstweaks.config;

import com.stalemated.lib.config.annotation.Comment;
import com.stalemated.lib.config.annotation.EnabledIf;
import com.stalemated.lib.config.annotation.Format;
import com.stalemated.lib.config.annotation.RangeFloat;
import com.stalemated.lib.config.annotation.Sync;
import com.stalemated.lib.config.annotation.Title;
import com.stalemated.lib.config.network.SyncMode;

@Sync(SyncMode.OVERRIDE_CLIENT)
public class SimplySkillsTweaksConfig {
    @Title("general")
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
    public boolean debugLogging = false;

    @Title("physical")
    @Comment("Physical attack damage multiplier up to the soft-cap threshold (Default: 0.50, Vanilla was 0.80)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 0.0f, max = 2.0f, step = 0.05f)
    public float physicalAttackMultiplier = 0.50f;

    @Comment("Attack damage threshold where physical soft-cap begins (Default: 120.0)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 20.0f, max = 500.0f, step = 5.0f)
    @Format("%.1f")
    public float physicalSoftCapThreshold = 120.0f;

    @Comment("Diminishing multiplier for attack damage exceeding the soft-cap threshold (Default: 0.20)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 0.0f, max = 1.0f, step = 0.05f)
    public float physicalExcessMultiplier = 0.20f;

    @Title("tank")
    @Comment("Residual attack damage multiplier for tanks (Default: 0.05)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 0.0f, max = 1.0f, step = 0.01f)
    public float tankAttackMultiplier = 0.05f;

    @Comment("Max Health multiplier for tanks (Default: 0.015)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 0.0f, max = 0.10f, step = 0.002f)
    public float tankHealthMultiplier = 0.015f;

    @Comment("Armor multiplier for tanks (Default: 0.30)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 0.0f, max = 2.0f, step = 0.05f)
    public float tankArmorMultiplier = 0.30f;

    @Comment("Bonus damage multiplier when holding a Shield in off-hand (Default: 1.35 = +35%)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 1.0f, max = 3.0f, step = 0.05f)
    public float shieldBonusMultiplier = 1.35f;

    @Title("holy_lightning")
    @Comment("Attack damage multiplier for Holy/Lightning hybrid builds (Default: 0.0)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 0.0f, max = 1.0f, step = 0.01f)
    public float holyLightningAttackMultiplier = 0.0f;

    @Comment("Spell power multiplier (max of Healing and Lightning power) (Default: 0.60)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 0.0f, max = 3.0f, step = 0.05f)
    public float holyLightningSpellMultiplier = 0.60f;

    @Comment("Armor multiplier for Holy/Lightning builds (Default: 0.20)")
    @EnabledIf("toggleNewHammerFormula")
    @RangeFloat(min = 0.0f, max = 2.0f, step = 0.05f)
    public float holyLightningArmorMultiplier = 0.20f;
}
