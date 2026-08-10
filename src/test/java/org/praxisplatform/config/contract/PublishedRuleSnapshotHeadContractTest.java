package org.praxisplatform.config.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.praxisplatform.rules.contract.DecisionAggregationPolicy;
import org.praxisplatform.rules.contract.DecisionBinding;
import org.praxisplatform.rules.contract.DecisionSlot;
import org.praxisplatform.rules.contract.DecisionSource;
import org.praxisplatform.rules.contract.DecisionStage;
import org.praxisplatform.rules.contract.OverridePolicy;
import org.praxisplatform.rules.contract.PublishedRuleSnapshot;
import org.praxisplatform.rules.contract.RuleDecision;
import org.praxisplatform.rules.contract.RuleExecutorRef;
import org.praxisplatform.rules.contract.RuleFailPolicy;
import org.praxisplatform.rules.contract.RuleRuntimeCompatibility;
import org.praxisplatform.rules.contract.RuleSetDefinition;
import org.praxisplatform.rules.contract.RuleSetRef;
import org.praxisplatform.rules.contract.RuleSnapshotApproval;
import org.praxisplatform.rules.contract.RuleSnapshotSource;
import org.praxisplatform.rules.contract.SlotCardinality;

class PublishedRuleSnapshotHeadContractTest {

    @Test
    void scopeIsExplicitAndNormalized() {
        PublishedRuleSnapshotHeadScope scope =
                new PublishedRuleSnapshotHeadScope(" tenant-a ", " development ", " ruleset-a ");

        assertEquals("tenant-a", scope.tenantId());
        assertEquals("development", scope.environment());
        assertEquals("ruleset-a", scope.ruleSetKey());
        assertThrows(IllegalArgumentException.class,
                () -> new PublishedRuleSnapshotHeadScope("tenant-a", " ", "ruleset-a"));
    }

    @Test
    void headKeepsImmutableContentIdentitySeparateFromMutableActivationIdentity() throws Exception {
        PublishedRuleSnapshot snapshot = snapshot();
        PublishedRuleSnapshotHead head = new PublishedRuleSnapshotHead(
                snapshot,
                "A".repeat(64),
                "opaque-head-2",
                2,
                PublishedRuleSnapshotHeadActivationType.ROLLED_BACK);

        assertEquals(snapshot, head.snapshot());
        assertEquals("A".repeat(64), head.snapshotContentHash());
        assertEquals("opaque-head-2", head.headEtag());
        assertEquals(2, head.activationRevision());
        assertEquals(PublishedRuleSnapshotHeadActivationType.ROLLED_BACK, head.activationType());
    }

    @Test
    void invalidHeadIdentityFailsClosed() throws Exception {
        PublishedRuleSnapshot snapshot = snapshot();

        assertThrows(IllegalArgumentException.class,
                () -> new PublishedRuleSnapshotHead(
                        snapshot, "a".repeat(64), "head", 1, PublishedRuleSnapshotHeadActivationType.ACTIVE));
        assertThrows(IllegalArgumentException.class,
                () -> new PublishedRuleSnapshotHead(
                        snapshot, "A".repeat(64), " ", 1, PublishedRuleSnapshotHeadActivationType.ACTIVE));
        assertThrows(IllegalArgumentException.class,
                () -> new PublishedRuleSnapshotHead(
                        snapshot, "A".repeat(64), "head", 0, PublishedRuleSnapshotHeadActivationType.ACTIVE));
        assertThrows(NullPointerException.class,
                () -> new PublishedRuleSnapshotHead(snapshot, "A".repeat(64), "head", 1, null));
    }

    @Test
    void activationTypeHasAClosedJacksonRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        for (PublishedRuleSnapshotHeadActivationType value
                : PublishedRuleSnapshotHeadActivationType.values()) {
            String json = mapper.writeValueAsString(value);
            assertEquals(value, mapper.readValue(json, PublishedRuleSnapshotHeadActivationType.class));
        }
        assertThrows(IllegalArgumentException.class,
                () -> PublishedRuleSnapshotHeadActivationType.valueOf("UNKNOWN"));
    }

    @Test
    void readerIsAFrameworkNeutralPort() throws Exception {
        PublishedRuleSnapshotHeadReader reader = scope -> Optional.empty();

        assertTrue(reader.findActive(
                new PublishedRuleSnapshotHeadScope("tenant-a", "development", "ruleset-a")).isEmpty());
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("org.springframework.context.ApplicationContext"));
        assertEquals(1, PublishedRuleSnapshotHeadReader.class.getDeclaredMethods().length);
    }

    private PublishedRuleSnapshot snapshot() throws Exception {
        DecisionSlot slot = new DecisionSlot(
                "eligibility",
                DecisionStage.DOMAIN_DECISION,
                SlotCardinality.SINGLE,
                OverridePolicy.FORBIDDEN,
                DecisionAggregationPolicy.SINGLE_RESULT);
        DecisionBinding binding = new DecisionBinding(
                "eligibility",
                "eligibility",
                DecisionSource.PRODUCT,
                null,
                RuleExecutorRef.jsonLogic(new ObjectMapper().readTree("{\"===\":[true,true]}")),
                List.of(),
                10,
                true,
                RuleDecision.DENY,
                "NOT_ELIGIBLE",
                List.of());
        RuleSetDefinition ruleSet = new RuleSetDefinition(
                new RuleSetRef("benefits", "grants", "extraordinary-grant", "evaluate", 1),
                List.of("request"),
                List.of(slot),
                List.of(binding),
                RuleRuntimeCompatibility.current(),
                RuleFailPolicy.FAIL_CLOSED);
        return new PublishedRuleSnapshot(
                PublishedRuleSnapshot.SNAPSHOT_CONTRACT_VERSION,
                "snapshot-a",
                "tenant-a",
                "development",
                "host-a",
                1,
                "2026-08-09T00:00:00Z",
                null,
                "host/1.0",
                "2026-08-09T00:00:00Z",
                null,
                List.of(new RuleSnapshotSource("source-a", "definition-a", 1, "A".repeat(64))),
                List.of(new RuleSnapshotApproval(
                        "approval-a",
                        "TECHNICAL_REVIEWER",
                        "actor:technical-reviewer",
                        "2026-08-09T00:00:00Z",
                        "B".repeat(64))),
                ruleSet);
    }
}
