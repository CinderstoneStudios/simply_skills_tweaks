package com.cinderstonestudios.simplyskillstweaks.util;

import com.cinderstonestudios.simplyskillstweaks.config.ConfigManager;
import com.cinderstonestudios.simplyskillstweaks.config.SSTConfig;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.cinderstonestudios.simplyskillstweaks.SimplySkillsTweaks.LOGGER;

/**
 * Service helper responsible for calculating the balanced damage scaling for the Righteous Hammers ability.
 * Supports three distinct archetypes via mutual exclusivity (max):
 * <ul>
 *  <li>Physical DPS (Warrior / Rogue, with soft-capped scaling)</li>
 *  <li>Tank (Pure Shield Tank / Blood Death Knight)</li>
 *  <li>Holy / Lightning Hybrid (Paladin / Valkyrie)</li>
 * </ul>
 */
public final class RighteousHammersDamageHelper {

    private static final Map<UUID, Long> LAST_LOG_TIMES = new ConcurrentHashMap<>();
    private static final long LOG_THROTTLE_MS = 500L;

    private RighteousHammersDamageHelper() {}

    /**
     * Calculates the final damage to be dealt by Righteous Hammers.
     *
     * @param source The damage source from which the attacker is retrieved.
     * @param originalAmount The baseline damage amount computed by SimplySkills.
     * @return The scaled damage amount.
     */
    public static float calculateDamage(DamageSource source, float originalAmount) {
        SSTConfig config = ConfigManager.getConfig();
        if (!config.toggleNewHammerFormula) return originalAmount;
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) {
            return originalAmount;
        }

        float attackDamage = (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float maxHealth = (float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        float armor = (float) player.getAttributeValue(EntityAttributes.GENERIC_ARMOR);

        // Physical Branch (with anti-exploit soft-cap)
        float physicalDamage;
        if (attackDamage <= config.physicalSoftCapThreshold) {
            physicalDamage = attackDamage * config.physicalAttackMultiplier;
        } else {
            physicalDamage = (config.physicalSoftCapThreshold * config.physicalAttackMultiplier)
                    + ((attackDamage - config.physicalSoftCapThreshold) * config.physicalExcessMultiplier);
        }

        // Tank Branch
        float tankBaseDamage = (attackDamage * config.tankAttackMultiplier)
                + (maxHealth * config.tankHealthMultiplier)
                + (armor * config.tankArmorMultiplier);

        boolean holdingShield = isHoldingShield(player);
        float tankMultiplier = holdingShield ? config.shieldBonusMultiplier : 1.0f;
        float tankDamage = tankBaseDamage * tankMultiplier;

        // Holy / Lightning Branch
        float elementalPower = getHighestElementalPower(player);
        float holyLightningDamage = (attackDamage * config.holyLightningAttackMultiplier)
                + (elementalPower * config.holyLightningSpellMultiplier)
                + (armor * config.holyLightningArmorMultiplier);

        // Mutual Exclusivity: Select the highest among all three paths
        float finalDamage = Math.max(physicalDamage, Math.max(tankDamage, holyLightningDamage));
        String winnerBranch = (finalDamage == physicalDamage) ? "Physical"
                : (finalDamage == tankDamage) ? "Tank" : "Holy/Lightning";

        if (config.debugLogging) {
            logDebugThrottled(player, attackDamage, maxHealth, armor, holdingShield, elementalPower,
                    physicalDamage, tankDamage, holyLightningDamage, winnerBranch, finalDamage);
        }

        return finalDamage;
    }

    private static void logDebugThrottled(ServerPlayerEntity player, float ad, float hp, float arm,
                                         boolean shield, float spellPwr, float physDmg, float tankDmg,
                                         float spellDmg, String winner, float finalDmg) {
        long now = System.currentTimeMillis();
        Long lastLog = LAST_LOG_TIMES.get(player.getUuid());

        if (lastLog == null || now - lastLog >= LOG_THROTTLE_MS) {
            LAST_LOG_TIMES.put(player.getUuid(), now);
            LOGGER.info("Player: {} | AD: {}, HP: {}, Arm: {}, Shield: {}, SpellPwr: {} | Phys: {}, Tank: {}, Spell: {} -> Winner: {} (Dealt: {})",
                    player.getName().getString(),
                    String.format(Locale.ROOT, "%.1f", ad),
                    String.format(Locale.ROOT, "%.1f", hp),
                    String.format(Locale.ROOT, "%.1f", arm),
                    shield,
                    String.format(Locale.ROOT, "%.1f", spellPwr),
                    String.format(Locale.ROOT, "%.2f", physDmg),
                    String.format(Locale.ROOT, "%.2f", tankDmg),
                    String.format(Locale.ROOT, "%.2f", spellDmg),
                    winner,
                    String.format(Locale.ROOT, "%.2f", finalDmg)
            );
        }
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
