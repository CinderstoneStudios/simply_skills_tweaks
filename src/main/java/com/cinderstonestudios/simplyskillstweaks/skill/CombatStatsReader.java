package com.cinderstonestudios.simplyskillstweaks.skill;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

public final class CombatStatsReader {
    private CombatStatsReader() {
    }

    public static CombatStats read(ServerPlayer player) {
        return new CombatStats(
                (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE),
                (float) player.getAttributeValue(Attributes.MAX_HEALTH),
                (float) player.getAttributeValue(Attributes.ARMOR),
                highestHolyLightningPower(player),
                isHoldingShield(player)
        );
    }

    /**
     * Retrieves the higher non-crit Spell Power value between the Healing and Lightning spell schools.
     *
     * @param player The caster server player.
     * @return The maximum Spell Power value.
     */
    private static float highestHolyLightningPower(ServerPlayer player) {
        float healingPower = (float) SpellPower.getSpellPower(SpellSchools.HEALING, player).baseValue();
        float lightningPower = (float) SpellPower.getSpellPower(SpellSchools.LIGHTNING, player).baseValue();
        return Math.max(healingPower, lightningPower);
    }

    /**
     * Checks if the player is holding a shield in their off-hand.
     * Checks both vanilla/modded ShieldItem instances and items matching the conventional 'c:shields' tag.
     *
     * @param player The player to inspect.
     * @return true if holding a shield in off-hand, false otherwise.
     */
    private static boolean isHoldingShield(ServerPlayer player) {
        ItemStack offhand = player.getOffhandItem();
        if (offhand.isEmpty()) return false;

        return offhand.getItem() instanceof ShieldItem || offhand.is(ConventionalItemTags.SHIELDS);
    }
}
