package com.cinderstonestudios.simplyskillstweaks.skill;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HammerDebugLogTest {
    private static final CombatStats STATS = new CombatStats(40, 20, 0, 0, false);
    private static final HammerBranchDamage DAMAGE = new HammerBranchDamage(20, 2.3f, 0);

    @Test
    void logsEachPlayerAtMostOnceEveryHalfSecond() {
        List<String> lines = new ArrayList<>();
        AtomicLong now = new AtomicLong(1_000);
        HammerDebugLog log = new HammerDebugLog(lines::add, now::get);
        UUID steve = UUID.randomUUID();
        UUID alex = UUID.randomUUID();

        log.log(steve, "Steve", STATS, DAMAGE);
        now.set(1_499);
        log.log(steve, "Steve", STATS, DAMAGE);
        log.log(alex, "Alex", STATS, DAMAGE);
        now.set(1_500);
        log.log(steve, "Steve", STATS, DAMAGE);

        assertEquals(List.of(
                "Player: Steve | AD: 40.0, HP: 20.0, Arm: 0.0, Shield: false, SpellPwr: 0.0 | Physical: 20.00, Tank: 2.30, Holy/Lightning: 0.00 -> Winner: Physical (Dealt: 20.00)",
                "Player: Alex | AD: 40.0, HP: 20.0, Arm: 0.0, Shield: false, SpellPwr: 0.0 | Physical: 20.00, Tank: 2.30, Holy/Lightning: 0.00 -> Winner: Physical (Dealt: 20.00)",
                "Player: Steve | AD: 40.0, HP: 20.0, Arm: 0.0, Shield: false, SpellPwr: 0.0 | Physical: 20.00, Tank: 2.30, Holy/Lightning: 0.00 -> Winner: Physical (Dealt: 20.00)"
        ), lines);
    }

    @Test
    void aPlayerWhoLeftIsLoggedRightAwayWhenTheyHitAgain() {
        List<String> lines = new ArrayList<>();
        AtomicLong now = new AtomicLong(1_000);
        HammerDebugLog log = new HammerDebugLog(lines::add, now::get);
        UUID steve = UUID.randomUUID();

        log.log(steve, "Steve", STATS, DAMAGE);
        log.forget(steve);
        now.set(1_001);
        log.log(steve, "Steve", STATS, DAMAGE);

        assertEquals(2, lines.size());
    }
}
