package com.cinderstonestudios.simplyskillstweaks.config;

import com.stalemated.lib.config.annotation.Comment;
import com.stalemated.lib.config.annotation.RangeFloat;

public class SSTConfig {
    @Comment("""
            Toggles the new Righteous Hammers formula.

            Formula breakdown:
              Final Damage = max(Physical Branch, Tank Branch, Holy/Lightning Branch)

              1. Physical Branch:
                 Damage = Attack Damage * physicalAttackMultiplier

              2. Tank Branch:
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


    @Comment("Physical attack damage multiplier for physical builds (Default: 0.25, Vanilla: 0.80)")
    @RangeFloat(min = 0.0f, max = 2.0f)
    public float physicalAttackMultiplier = 0.25f;


    @Comment("Residual attack damage multiplier for tanks (Default: 0.05)")
    @RangeFloat(min = 0.0f, max = 1.0f)
    public float tankAttackMultiplier = 0.05f;

    @Comment("Max Health multiplier for tanks (Default: 0.035)")
    @RangeFloat(min = 0.0f, max = 0.5f)
    public float tankHealthMultiplier = 0.035f;

    @Comment("Armor multiplier for tanks (Default: 0.30)")
    @RangeFloat(min = 0.0f, max = 2.0f)
    public float tankArmorMultiplier = 0.30f;

    @Comment("Bonus damage multiplier when holding a Shield in off-hand (Default: 1.20)")
    @RangeFloat(min = 1.0f, max = 3.0f)
    public float shieldBonusMultiplier = 1.20f;


    @Comment("Attack damage multiplier for Holy/Lightning hybrid builds (Default: 0.10)")
    @RangeFloat(min = 0.0f, max = 1.0f)
    public float holyLightningAttackMultiplier = 0.10f;

    @Comment("Spell power multiplier (max of Healing and Lightning power) (Default: 0.9)")
    @RangeFloat(min = 0.0f, max = 3.0f)
    public float holyLightningSpellMultiplier = 0.9f;

    @Comment("Armor multiplier for Holy/Lightning builds (Default: 0.20)")
    @RangeFloat(min = 0.0f, max = 2.0f)
    public float holyLightningArmorMultiplier = 0.20f;
}
