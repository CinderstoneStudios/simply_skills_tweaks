package com.cinderstonestudios.simplyskillstweaks.skill;

public record HammerBranchDamage(float physical, float tank, float holyLightning) {
    public HammerBranch winner() {
        HammerBranch winner = HammerBranch.PHYSICAL;
        for (HammerBranch branch : HammerBranch.values())
            if (damageOf(branch) > damageOf(winner)) winner = branch;
        return winner;
    }

    public float amount() {
        return damageOf(winner());
    }

    public float damageOf(HammerBranch branch) {
        return switch (branch) {
            case PHYSICAL -> physical;
            case TANK -> tank;
            case HOLY_LIGHTNING -> holyLightning;
        };
    }
}
