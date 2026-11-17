package com.cinderstonestudios.simplyskillstweaks.skill;

import com.cinderstonestudios.simplyskillstweaks.SimplySkillsTweaks;
import com.cinderstonestudios.simplyskillstweaks.config.SimplySkillsTweaksConfig;
import com.cinderstonestudios.simplyskillstweaks.config.SimplySkillsTweaksConfigRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public final class RighteousHammersDamage {
    private static final HammerDebugLog DEBUG_LOG = new HammerDebugLog(SimplySkillsTweaks.LOGGER::info, System::currentTimeMillis);

    private RighteousHammersDamage() {
    }

    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> DEBUG_LOG.forget(handler.player.getUUID()));
    }

    /**
     * Calculates the final damage to be dealt by Righteous Hammers.
     *
     * @param source The damage source from which the attacker is retrieved.
     * @param originalAmount The baseline damage amount computed by SimplySkills.
     * @return The scaled damage amount, or {@code originalAmount} when the new formula is off or the attacker is not a player.
     */
    public static float scale(DamageSource source, float originalAmount) {
        SimplySkillsTweaksConfig config = SimplySkillsTweaksConfigRegistry.CONFIG.get();
        if (!config.toggleNewHammerFormula) return originalAmount;
        if (!(source.getEntity() instanceof ServerPlayer player)) return originalAmount;

        CombatStats stats = CombatStatsReader.read(player);
        HammerBranchDamage damage = RighteousHammersFormula.calculate(config, stats);
        if (config.debugLogging) DEBUG_LOG.log(player.getUUID(), player.getName().getString(), stats, damage);
        return damage.amount();
    }
}
