package org.praxisplatform.config.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainRuleTestRunContractTest {

    @Test
    void requiresAndNormalizesTheIdempotencyKey() {
        var request = new DomainRuleTestRunRecordRequest(
                "  host-run-42  ", 1, "A".repeat(64), Instant.EPOCH, "UTC",
                null, null, 0, null, List.of());

        assertEquals("host-run-42", request.idempotencyKey());
        assertThrows(IllegalArgumentException.class, () -> new DomainRuleTestRunRecordRequest(
                " ", 1, "A".repeat(64), Instant.EPOCH, "UTC", null, null, 0, null, List.of()));
    }

    @Test
    void defensivelyCopiesScenarioAndBaselineAssertions() {
        var reasons = new ArrayList<>(List.of("LEGACY_ALLOW"));
        var baseline = new DomainRuleTestBaselineResult(
                "ALLOW", null, reasons, List.of(), "A".repeat(64), "B".repeat(64), null);
        var scenario = new DomainRuleTestRunResultRequest(
                UUID.randomUUID(), "create-allow", "ALLOW", "ALLOW", null, null,
                reasons, List.of(), List.of(), List.of(), "A".repeat(64), "B".repeat(64),
                "C".repeat(64), baseline, null);
        var request = new DomainRuleTestRunRecordRequest(
                "run-1", 1, "D".repeat(64), Instant.EPOCH, "UTC", null, null, 0,
                new DomainRuleTestBaselineEvidence(
                        "LEGACY_ORACLE", "oracle-proof", "E".repeat(64), Instant.EPOCH, "ELIGIBLE"),
                new ArrayList<>(List.of(scenario)));

        reasons.add("MUTATED");

        assertEquals(List.of("LEGACY_ALLOW"), request.results().getFirst().candidateReasonCodes());
        assertEquals(List.of("LEGACY_ALLOW"), request.results().getFirst().baselineResult().reasonCodes());
    }
}
