package org.praxisplatform.config.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    void publicationReceiptSerializesOnlyImmutableIdentityWithoutClaimingActivation() throws Exception {
        var publication = new RuleSetCompositionPublication(
                "snapshot-2", "ruleset-a", 2, "C".repeat(64));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode wire = mapper.valueToTree(publication);
        Set<String> fields = new HashSet<>();
        wire.fieldNames().forEachRemaining(fields::add);

        assertEquals(Set.of("snapshotKey", "ruleSetKey", "ruleSetVersion", "snapshotContentHash"), fields);
        assertEquals("snapshot-2", wire.path("snapshotKey").asText());
        assertEquals("ruleset-a", wire.path("ruleSetKey").asText());
        assertEquals(2, wire.path("ruleSetVersion").asInt());
        assertEquals("C".repeat(64), wire.path("snapshotContentHash").asText());
        assertEquals(publication, mapper.treeToValue(wire, RuleSetCompositionPublication.class));
    }

    @Test
    void publicationReceiptNormalizesIdentityButRejectsMissingOrInvalidContentIdentity() {
        var publication = new RuleSetCompositionPublication(
                " snapshot-2 ", " ruleset-a ", 2, " " + DIGEST + " ");
        assertEquals("snapshot-2", publication.snapshotKey());
        assertEquals("ruleset-a", publication.ruleSetKey());
        assertEquals(DIGEST, publication.snapshotContentHash());

        assertThrows(NullPointerException.class, () -> new RuleSetCompositionPublication(
                null, "ruleset-a", 2, DIGEST));
        assertThrows(NullPointerException.class, () -> new RuleSetCompositionPublication(
                "snapshot-2", null, 2, DIGEST));
        assertThrows(NullPointerException.class, () -> new RuleSetCompositionPublication(
                "snapshot-2", "ruleset-a", 2, null));
        assertThrows(IllegalArgumentException.class, () -> new RuleSetCompositionPublication(
                " ", "ruleset-a", 2, DIGEST));
        assertThrows(IllegalArgumentException.class, () -> new RuleSetCompositionPublication(
                "snapshot-2", " ", 2, DIGEST));
        for (int version : new int[]{0, -1}) {
            assertThrows(IllegalArgumentException.class, () -> new RuleSetCompositionPublication(
                    "snapshot-2", "ruleset-a", version, DIGEST));
        }
        for (String invalidHash : List.of(" ", "bad", "a".repeat(64), "A".repeat(63), "G".repeat(64))) {
            assertThrows(IllegalArgumentException.class, () -> new RuleSetCompositionPublication(
                    "snapshot-2", "ruleset-a", 2, invalidHash));
        }
    }
}
