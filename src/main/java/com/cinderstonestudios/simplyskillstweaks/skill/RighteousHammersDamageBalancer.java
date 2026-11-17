package com.cinderstonestudios.simplyskillstweaks.skill;

import com.cinderstonestudios.simplyskillstweaks.config.SimplySkillsTweaksConfig;
import com.cinderstonestudios.simplyskillstweaks.config.SimplySkillsTweaksConfigRegistry;
import com.cinderstonestudios.simplyskillstweaks.util.PlayerStatsUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.spell_power.api.SpellSchools;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.cinderstonestudios.simplyskillstweaks.SimplySkillsTweaks.LOGGER;

/**
 * Class responsible for calculating the balanced damage scaling for the Righteous Hammers ability.
 * Supports three distinct archetypes via mutual exclusivity (max):
 * <ul>
 *  <li>Physical DPS (Warrior / Rogue, with soft-capped scaling)</li>
 *  <li>Tank (Pure Shield Tank / Blood Death Knight)</li>
 *  <li>Holy / Lightning Hybrid (Paladin / Valkyrie)</li>
 * </ul>
 */
public final class RighteousHammersDamageBalancer {

    private static final Map<UUID, Long> LAST_LOG_TIMES = new ConcurrentHashMap<>();
    private static final long LOG_THROTTLE_MS = 500L;

    private RighteousHammersDamageBalancer() {}

    /**
     * Calculates the final damage to be dealt by Righteous Hammers.
     *
     * @param source The damage source from which the attacker is retrieved.
     * @param originalAmount The baseline damage amount computed by SimplySkills.
     * @return The scaled damage amount.
     */
    public static float calculateDamage(DamageSource source, float originalAmount) {
        SimplySkillsTweaksConfig config = SimplySkillsTweaksConfigRegistry.CONFIG.get();
        if (!config.toggleNewHammerFormula) return originalAmount;
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return originalAmount;
        }

        float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float maxHealth = (float) player.getAttributeValue(Attributes.MAX_HEALTH);
        float armor = (float) player.getAttributeValue(Attributes.ARMOR);
        boolean holdingShield = PlayerStatsUtils.isHoldingShield(player);
        float spellPower = PlayerStatsUtils.getHighestSpellPower(player, SpellSchools.HEALING, SpellSchools.LIGHTNING);

        float physicalDamage = getPhysicalBranchDamage(config, attackDamage);
        float tankDamage = getTankBranchDamage(config, attackDamage, maxHealth, armor, holdingShield);
        float holyLightningDamage = getSpellBranchDamage(config, attackDamage, spellPower, armor);

        float finalDamage = Math.max(physicalDamage, Math.max(tankDamage, holyLightningDamage));

        if (config.debugLogging) {
            String winnerBranch = (finalDamage == physicalDamage) ? "Physical"
                    : (finalDamage == tankDamage) ? "Tank" : "Holy/Lightning";

            logDebugThrottled(player, attackDamage, maxHealth, armor, holdingShield, spellPower,
                    physicalDamage, tankDamage, holyLightningDamage, winnerBranch, finalDamage);
        }

        return finalDamage;
    }

    private static float getPhysicalBranchDamage(SimplySkillsTweaksConfig config, float attackDamage) {
        if (attackDamage <= config.physicalSoftCapThreshold) {
            return attackDamage * config.physicalAttackMultiplier;
        } else {
            return (config.physicalSoftCapThreshold * config.physicalAttackMultiplier)
                    + ((attackDamage - config.physicalSoftCapThreshold) * config.physicalExcessMultiplier);
        }
    }

    private static float getTankBranchDamage(SimplySkillsTweaksConfig config, float attackDamage, float maxHealth, float armor, boolean holdingShield) {
        float tankBaseDamage = (attackDamage * config.tankAttackMultiplier)
                + (maxHealth * config.tankHealthMultiplier)
                + (armor * config.tankArmorMultiplier);

        float tankMultiplier = holdingShield ? config.shieldBonusMultiplier : 1.0f;
        return tankBaseDamage * tankMultiplier;
    }

    private static float getSpellBranchDamage(SimplySkillsTweaksConfig config, float attackDamage, float elementalPower, float armor) {
        return (attackDamage * config.holyLightningAttackMultiplier)
                + (elementalPower * config.holyLightningSpellMultiplier)
                + (armor * config.holyLightningArmorMultiplier);
    }

    private static void logDebugThrottled(
            ServerPlayer player, float ad, float hp, float arm,
            boolean shield, float spellPwr, float physDmg, float tankDmg,
            float spellDmg, String winner, float finalDmg
    ) {
        long now = System.currentTimeMillis();
        Long lastLog = LAST_LOG_TIMES.get(player.getUUID());

        if (lastLog == null || now - lastLog >= LOG_THROTTLE_MS) {
            LAST_LOG_TIMES.put(player.getUUID(), now);
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
}
