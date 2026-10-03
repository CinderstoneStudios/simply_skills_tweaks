package com.cinderstonestudios.simplyskillstweaks.util;

import com.cinderstonestudios.simplyskillstweaks.config.ConfigManager;
import com.cinderstonestudios.simplyskillstweaks.config.SSTConfig;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

/**
 * Service helper responsible for calculating the balanced damage scaling for the Righteous Hammers ability.
 * Supports three distinct archetypes via mutual exclusivity (max):
 * <ul>
 *  <li>Physical DPS (Warrior / Rogue)</li>
 *  <li>Tank (Pure Shield Tank / Blood Death Knight)</li>
 *  <li>Holy / Lightning Hybrid (Paladin / Valkyrie)</li>
 * </ul>
 */
public final class RighteousHammersDamageHelper {

    private RighteousHammersDamageHelper() {}

    /**
     * Calculates the final damage to be dealt by Righteous Hammers.
     *
     * @param target The entity receiving damage.
     * @param source The damage source from which the attacker is retrieved.
     * @param originalAmount The baseline damage amount computed by SimplySkills.
     * @return The scaled damage amount.
     */
    public static float calculateDamage(LivingEntity target, DamageSource source, float originalAmount) {
        SSTConfig config = ConfigManager.getConfig();
        if (!config.toggleNewHammerFormula) return originalAmount;
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return originalAmount;

        float attackDamage = (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float maxHealth = (float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        float armor = (float) player.getAttributeValue(EntityAttributes.GENERIC_ARMOR);

        // Physical Branch
        float physicalDamage = attackDamage * config.physicalAttackMultiplier;

        // Tank Branch
        float tankBaseDamage = (attackDamage * config.tankAttackMultiplier)
                + (maxHealth * config.tankHealthMultiplier)
                + (armor * config.tankArmorMultiplier);

        float tankMultiplier = isHoldingShield(player) ? config.shieldBonusMultiplier : 1.0f;
        float tankDamage = tankBaseDamage * tankMultiplier;

        // Holy / Lightning Branch
        float elementalPower = getHighestElementalPower(player);
        float holyLightningDamage = (attackDamage * config.holyLightningAttackMultiplier)
                + (elementalPower * config.holyLightningSpellMultiplier)
                + (armor * config.holyLightningArmorMultiplier);

        return Math.max(physicalDamage, Math.max(tankDamage, holyLightningDamage));
    }

    /**
     * Retrieves the higher value between Healing (Holy) Spell Power and Lightning Spell Power.
     *
     * @param player The caster server player.
     * @return The maximum elemental power value.
     */
    public static float getHighestElementalPower(ServerPlayerEntity player) {
        try {
            float healingPower = (float) SpellPower.getSpellPower(SpellSchools.HEALING, player).baseValue();
            float lightningPower = (float) SpellPower.getSpellPower(SpellSchools.LIGHTNING, player).baseValue();
            return Math.max(healingPower, lightningPower);
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
    public static boolean isHoldingShield(ServerPlayerEntity player) {
        ItemStack offHand = player.getOffHandStack();
        if (offHand.isEmpty()) {
            return false;
        }
        return offHand.getItem() instanceof ShieldItem || offHand.isIn(ConventionalItemTags.SHIELDS);
    }
}
