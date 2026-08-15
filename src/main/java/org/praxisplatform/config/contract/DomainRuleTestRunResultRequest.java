package org.praxisplatform.config.contract;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.UUID;

/**
 * Redacted candidate, active, baseline and operational evidence for one governed scenario.
 *
 * @param scenarioId governed scenario identifier
 * @param scenarioKey stable scenario key
 * @param candidateDecision candidate five-state decision
 * @param activeDecision active-snapshot five-state decision
 * @param candidateOutput redacted candidate output
 * @param activeOutput redacted active output
 * @param candidateReasonCodes candidate reason codes
 * @param activeReasonCodes active reason codes
 * @param candidateEffectIntents candidate effect intents
 * @param activeEffectIntents active effect intents
 * @param candidatePlanDigest candidate plan SHA-256
 * @param activePlanDigest active plan SHA-256
 * @param factsDigest redacted facts SHA-256
 * @param baselineResult independent baseline lane result
 * @param operationalEvidence host-observed mutation, cleanup and effect evidence
 */
public record DomainRuleTestRunResultRequest(
        UUID scenarioId,
        String scenarioKey,
        String candidateDecision,
        String activeDecision,
        JsonNode candidateOutput,
        JsonNode activeOutput,
        List<String> candidateReasonCodes,
        List<String> activeReasonCodes,
        List<String> candidateEffectIntents,
        List<String> activeEffectIntents,
        String candidatePlanDigest,
        String activePlanDigest,
        String factsDigest,
        DomainRuleTestBaselineResult baselineResult,
        DomainRuleOperationalTestEvidence operationalEvidence) {

    /** Defensively copies assertion collections before transport. */
    public DomainRuleTestRunResultRequest {
        candidateReasonCodes = copy(candidateReasonCodes);
        activeReasonCodes = copy(activeReasonCodes);
        candidateEffectIntents = copy(candidateEffectIntents);
        activeEffectIntents = copy(activeEffectIntents);
    }

    private static List<String> copy(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
