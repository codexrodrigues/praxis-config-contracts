package org.praxisplatform.config.contract;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.praxisplatform.rules.contract.*;

class PublishedRuleSnapshotHeadSetTest {
    @Test void normalizesASetWithoutPermittingAmbiguousOrUnboundedSelection() {
        var scope = scope(List.of("update", "create"));
        assertEquals(List.of("create", "update"), scope.operationKeys());
        assertThrows(IllegalArgumentException.class, () -> scope(List.of("create", " create ")));
        assertThrows(IllegalArgumentException.class, () -> scope(List.of()));
        assertThrows(IllegalArgumentException.class, () -> scope(Collections.nCopies(65, "create")));
        assertThrows(IllegalArgumentException.class, () -> scope(List.of(" ")));
    }
    @Test void partialOrMixedScopeHeadsCannotBecomeACompleteSelection() throws Exception {
        var scope = scope(List.of("create", "update"));
        assertThrows(IllegalArgumentException.class, () -> new PublishedRuleSnapshotHeadSet(scope, Map.of("create", head("create", "a", 1))));
        assertThrows(IllegalArgumentException.class, () -> new PublishedRuleSnapshotHeadSet(scope,
                Map.of("create", head("create", "a", 1), "update", head("delete", "b", 1))));
        assertThrows(IllegalArgumentException.class, () -> new PublishedRuleSnapshotHeadSet(
                new PublishedRuleSnapshotHeadSetScope("other", "stage", "family", List.of("create")), Map.of("create", head("create", "a", 1))));
    }
    @Test void fingerprintBindsActivationIdentityEvenWhenContentReturnsToTheSameHash() throws Exception {
        var scope = scope(List.of("create"));
        var first = new PublishedRuleSnapshotHeadSet(scope, Map.of("create", head("create", "a", 1)));
        var reactivated = new PublishedRuleSnapshotHeadSet(scope, Map.of("create", head("create", "b", 3)));
        assertNotEquals(first.fingerprint(), reactivated.fingerprint());
        assertEquals(first.fingerprint(), new PublishedRuleSnapshotHeadSet(scope, Map.of("create", head("create", "a", 1))).fingerprint());
    }
    @Test void selectionDoesNotRetainTheCallersMutableMap() throws Exception {
        var map = new HashMap<String, PublishedRuleSnapshotHead>(); map.put("create", head("create", "a", 1));
        var selection = new PublishedRuleSnapshotHeadSet(scope(List.of("create")), map); map.clear();
        assertEquals(1, selection.heads().size());
        assertThrows(UnsupportedOperationException.class, () -> selection.heads().clear());
    }
    private static PublishedRuleSnapshotHeadSetScope scope(List<String> operations) {
        return new PublishedRuleSnapshotHeadSetScope("2", "stage", "family", operations);
    }
    private static PublishedRuleSnapshotHead head(String operation, String etag, long revision) throws Exception {
        var slot = new DecisionSlot("allow", DecisionStage.DOMAIN_DECISION, SlotCardinality.SINGLE,
                OverridePolicy.FORBIDDEN, DecisionAggregationPolicy.SINGLE_RESULT, null, List.of());
        var binding = new DecisionBinding("allow", "allow", DecisionSource.PRODUCT, null,
                RuleExecutorRef.jsonLogic(new ObjectMapper().readTree("{\"var\":\"context.allowed\"}")), List.of(), 1, true,
                RuleDecision.DENY, "DENIED", List.of(), null);
        var definition = new RuleSetDefinition(new RuleSetRef("domain", "context", "family", operation, 1),
                List.of("context"), List.of(slot), List.of(binding), RuleRuntimeCompatibility.current(), RuleFailPolicy.FAIL_CLOSED);
        var snapshot = new PublishedRuleSnapshot(PublishedRuleSnapshot.SNAPSHOT_CONTRACT_VERSION, "snapshot-"+operation,
                "2", "stage", "host", 1, "2026-11-05T00:00:00Z", null, "host/2.0", "2026-11-05T00:00:00Z", null,
                List.of(new RuleSnapshotSource("source", "definition", 1, "A".repeat(64))),
                List.of(new RuleSnapshotApproval("approval", "REVIEWER", "reviewer", "2026-11-05T00:00:00Z", "B".repeat(64))), definition);
        return new PublishedRuleSnapshotHead(snapshot, "C".repeat(64), etag, revision, PublishedRuleSnapshotHeadActivationType.ACTIVE);
    }
}
