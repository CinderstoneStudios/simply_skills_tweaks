package com.cinderstonestudios.simplyskillstweaks.util;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;

public class PlayerStatsUtils {

    /**
     * Retrieves the higher non-crit Spell Power value between two spell schools.
     *
     * @param player The caster server player.
     * @return The maximum Spell Power value.
     */
    public static float getHighestSpellPower(ServerPlayer player, SpellSchool school1, SpellSchool school2) {
        try {
            float spellPower1 = (float) SpellPower.getSpellPower(school1, player).baseValue();
            float spellPower2 = (float) SpellPower.getSpellPower(school2, player).baseValue();
            return Math.max(spellPower1, spellPower2);
        } catch (Throwable ignored) {
            return 0.0f;
        }
    }

    /**
     * Checks if the player is holding a shield in their off-hand.
     * Checks both vanilla/modded ShieldItem instances and items matching the conventional 'c:shields' tag.
     *
     * @param player The player to inspect.
     * @return true if holding a shield in off-hand, false otherwise.
     */
    public static boolean isHoldingShield(ServerPlayer player) {
        ItemStack offHand = player.getOffhandItem();
        if (offHand.isEmpty()) return false;

        return offHand.getItem() instanceof ShieldItem || offHand.is(ConventionalItemTags.SHIELDS);
    }
}
