package com.cinderstonestudios.simplyskillstweaks.skill;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

public final class HammerDebugLog {
    private static final long INTERVAL_MS = 500L;

    private final Map<UUID, Long> lastLogTimes = new ConcurrentHashMap<>();
    private final Consumer<String> output;
    private final LongSupplier clockMillis;

    public HammerDebugLog(Consumer<String> output, LongSupplier clockMillis) {
        this.output = output;
        this.clockMillis = clockMillis;
    }

    public void log(UUID playerId, String playerName, CombatStats stats, HammerBranchDamage damage) {
        long now = clockMillis.getAsLong();
        Long lastLog = lastLogTimes.get(playerId);
        if (lastLog != null && now - lastLog < INTERVAL_MS) return;

        lastLogTimes.put(playerId, now);
        output.accept(describe(playerName, stats, damage));
    }

    public void forget(UUID playerId) {
        lastLogTimes.remove(playerId);
    }

    private static String describe(String playerName, CombatStats stats, HammerBranchDamage damage) {
        return String.format(Locale.ROOT,
                "Player: %s | AD: %.1f, HP: %.1f, Arm: %.1f, Shield: %s, SpellPwr: %.1f | Physical: %.2f, Tank: %.2f, Holy/Lightning: %.2f -> Winner: %s (Dealt: %.2f)",
                playerName, stats.attackDamage(), stats.maxHealth(), stats.armor(), stats.holdingShield(), stats.spellPower(),
                damage.physical(), damage.tank(), damage.holyLightning(), damage.winner().label(), damage.amount());
    }
}
