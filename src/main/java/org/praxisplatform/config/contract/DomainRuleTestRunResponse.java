package org.praxisplatform.config.contract;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Safe idempotent receipt for one persisted governed policy Test Run.
 *
 * @param runId immutable run identifier
 * @param workspaceId governed workspace identifier
 * @param idempotencyKey accepted retry key
 * @param requestHash canonical semantic request hash
 * @param workspaceRevision proved workspace revision
 * @param baseDefinitionHash proved base-definition SHA-256
 * @param evaluatedAtUtc host evaluation instant
 * @param userTimeZone IANA time zone used by the suite
 * @param activeSnapshotKey active snapshot key, when available
 * @param activeSnapshotContentHash active snapshot SHA-256, when available
 * @param activeActivationRevision active snapshot-head revision
 * @param baselineEvidence safe independent-baseline provenance
 * @param results immutable per-scenario receipts
 * @param recordedBy authenticated actor reference
 * @param recordedAt persistence instant
 */
public record DomainRuleTestRunResponse(
        UUID runId,
        UUID workspaceId,
        String idempotencyKey,
        String requestHash,
        long workspaceRevision,
        String baseDefinitionHash,
        Instant evaluatedAtUtc,
        String userTimeZone,
        String activeSnapshotKey,
        String activeSnapshotContentHash,
        long activeActivationRevision,
        DomainRuleTestBaselineEvidence baselineEvidence,
        List<DomainRuleTestRunResultResponse> results,
        String recordedBy,
        Instant recordedAt) {

    /** Defensively copies scenario receipts. */
    public DomainRuleTestRunResponse {
        results = results == null ? List.of() : List.copyOf(results);
    }
}
