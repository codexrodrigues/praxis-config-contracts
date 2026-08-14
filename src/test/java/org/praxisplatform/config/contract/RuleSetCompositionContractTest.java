package org.praxisplatform.config.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RuleSetCompositionContractTest {
    private static final UUID DEFINITION = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String DIGEST = "A".repeat(64);

    @Test
    void candidateCommandIsDigestBoundAndNormalizesItsRequest() {
        var command = new RuleSetCompositionCandidateCommand(
                DEFINITION, " 2026-08-14T00:00:00Z ", " ", DIGEST);

        assertEquals("2026-08-14T00:00:00Z", command.validFromUtc());
        assertEquals(null, command.validUntilUtc());
        assertEquals(command.candidateRequest(), new RuleSetCompositionCandidateRequest(
                DEFINITION, "2026-08-14T00:00:00Z", null));
        assertThrows(IllegalArgumentException.class, () -> new RuleSetCompositionCandidateCommand(
                DEFINITION, "2026-08-14T00:00:00Z", null, "not-a-digest"));
    }

    @Test
    void safeCandidateDoesNotCarryExecutableRuleContent() {
        var candidate = new RuleSetCompositionCandidate(
                "ruleset-a", 2, DIGEST, "B".repeat(64), "head-1",
                List.of(new RuleSetCompositionSource(DEFINITION, "decision-a", 3, "approved")),
                List.of(RuleSetCompositionAction.PREPARE, RuleSetCompositionAction.PUBLISH));

        assertEquals(7, RuleSetCompositionCandidate.class.getRecordComponents().length);
        assertEquals(List.of(RuleSetCompositionAction.PREPARE, RuleSetCompositionAction.PUBLISH),
                candidate.authorizedActions());
        assertEquals(List.of(), new RuleSetCompositionCandidate(
                "ruleset-a", 2, DIGEST, "B".repeat(64), null,
                List.of(new RuleSetCompositionSource(DEFINITION, "decision-a", 3, "approved")),
                List.of()).authorizedActions());
        assertThrows(IllegalArgumentException.class, () -> new RuleSetCompositionCandidate(
                "ruleset-a", 2, DIGEST, "B".repeat(64), null,
                List.of(new RuleSetCompositionSource(DEFINITION, "decision-a", 3, "approved")),
                List.of(RuleSetCompositionAction.PREPARE, RuleSetCompositionAction.PREPARE)));
    }

    @Test
    void publicationSeparatesSnapshotHashFromMutableHeadIdentity() {
        var publication = new RuleSetCompositionPublication(
                "snapshot-2", "ruleset-a", 2, "C".repeat(64), "head-2", 2,
                PublishedRuleSnapshotHeadActivationType.PUBLISHED);

        assertEquals("C".repeat(64), publication.snapshotContentHash());
        assertEquals("head-2", publication.headEtag());
        assertThrows(IllegalArgumentException.class, () -> new RuleSetCompositionPublication(
                "snapshot-2", "ruleset-a", 2, "bad", "head-2", 2,
                PublishedRuleSnapshotHeadActivationType.PUBLISHED));
    }
}
