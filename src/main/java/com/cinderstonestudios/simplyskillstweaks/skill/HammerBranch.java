package com.cinderstonestudios.simplyskillstweaks.skill;

public enum HammerBranch {
    PHYSICAL("Physical"),
    TANK("Tank"),
    HOLY_LIGHTNING("Holy/Lightning");

    private final String label;

    HammerBranch(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
