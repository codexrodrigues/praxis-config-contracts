package org.praxisplatform.config.contract;

import java.time.Instant;
import java.util.List;

/**
 * Idempotent host command for recording one complete governed policy Test Run.
 *
 * <p>The authenticated adapter owns tenant, environment and actor identity. Callers must reuse the
 * same idempotency key only for the exact same semantic payload.</p>
 *
 * @param idempotencyKey stable retry key for this semantic command
 * @param workspaceRevision governed workspace revision being proved
 * @param baseDefinitionHash SHA-256 of the immutable base definition
 * @param evaluatedAtUtc instant at which the host evaluated the run
 * @param userTimeZone IANA time zone used by the scenario suite
 * @param activeSnapshotKey active snapshot key, when available
 * @param activeSnapshotContentHash active snapshot SHA-256, when available
 * @param activeActivationRevision active snapshot-head revision
 * @param baselineEvidence safe provenance of the independent baseline authority
 * @param results complete scenario result set for this run
 */
public record DomainRuleTestRunRecordRequest(
        String idempotencyKey,
        long workspaceRevision,
        String baseDefinitionHash,
        Instant evaluatedAtUtc,
        String userTimeZone,
        String activeSnapshotKey,
        String activeSnapshotContentHash,
        long activeActivationRevision,
        DomainRuleTestBaselineEvidence baselineEvidence,
        List<DomainRuleTestRunResultRequest> results) {

    /** Normalizes the idempotency key and defensively copies scenario results. */
    public DomainRuleTestRunRecordRequest {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey is required");
        }
        idempotencyKey = idempotencyKey.trim();
        if (idempotencyKey.length() > 180) {
            throw new IllegalArgumentException("idempotencyKey exceeds 180 characters");
        }
        results = results == null ? List.of() : List.copyOf(results);
    }
}
