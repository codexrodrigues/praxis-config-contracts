package org.praxisplatform.config.contract;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.UUID;

/**
 * Safe persisted receipt for one governed Test Run scenario.
 *
 * @param scenarioId governed scenario identifier
 * @param scenarioKey stable scenario key
 * @param expectedDecision governed expected decision
 * @param candidateDecision candidate decision
 * @param activeDecision active-snapshot decision
 * @param comparison candidate-to-active comparison
 * @param candidateMatchesExpected whether the candidate decision matches
 * @param activeMatchesExpected whether the active decision matches
 * @param expectedOutput governed redacted expected output
 * @param candidateOutput candidate redacted output
 * @param activeOutput active redacted output
 * @param candidateOutputMatchesExpected whether candidate output matches
 * @param activeOutputMatchesExpected whether active output matches
 * @param expectedReasonCodes governed expected reason codes
 * @param candidateReasonCodes candidate reason codes
 * @param activeReasonCodes active reason codes
 * @param candidateReasonCodesMatchExpected whether candidate reasons match
 * @param activeReasonCodesMatchExpected whether active reasons match
 * @param expectedEffectIntents governed expected effect intents
 * @param candidateEffectIntents candidate effect intents
 * @param activeEffectIntents active effect intents
 * @param candidateEffectsMatchExpected whether candidate effects match
 * @param activeEffectsMatchExpected whether active effects match
 * @param candidatePlanDigest candidate plan SHA-256
 * @param activePlanDigest active plan SHA-256
 * @param factsDigest redacted facts SHA-256
 * @param baselineResult independent baseline lane result
 * @param candidateBaselineComparison candidate-to-baseline comparison
 * @param baselineMatchesExpected whether the baseline decision matches
 * @param baselineOutputMatchesExpected whether the baseline output matches
 * @param baselineReasonCodesMatchExpected whether baseline reasons match
 * @param baselineEffectsMatchExpected whether baseline effects match
 * @param operationalEvidence host-observed mutation, cleanup and effect evidence
 */
public record DomainRuleTestRunResultResponse(
        UUID scenarioId,
        String scenarioKey,
        String expectedDecision,
        String candidateDecision,
        String activeDecision,
        String comparison,
        boolean candidateMatchesExpected,
        boolean activeMatchesExpected,
        JsonNode expectedOutput,
        JsonNode candidateOutput,
        JsonNode activeOutput,
        boolean candidateOutputMatchesExpected,
        boolean activeOutputMatchesExpected,
        List<String> expectedReasonCodes,
        List<String> candidateReasonCodes,
        List<String> activeReasonCodes,
        boolean candidateReasonCodesMatchExpected,
        boolean activeReasonCodesMatchExpected,
        List<String> expectedEffectIntents,
        List<String> candidateEffectIntents,
        List<String> activeEffectIntents,
        boolean candidateEffectsMatchExpected,
        boolean activeEffectsMatchExpected,
        String candidatePlanDigest,
        String activePlanDigest,
        String factsDigest,
        DomainRuleTestBaselineResult baselineResult,
        String candidateBaselineComparison,
        boolean baselineMatchesExpected,
        boolean baselineOutputMatchesExpected,
        boolean baselineReasonCodesMatchExpected,
        boolean baselineEffectsMatchExpected,
        DomainRuleOperationalTestEvidence operationalEvidence) {

    /** Defensively copies all assertion collections in the receipt. */
    public DomainRuleTestRunResultResponse {
        expectedReasonCodes = copy(expectedReasonCodes);
        candidateReasonCodes = copy(candidateReasonCodes);
        activeReasonCodes = copy(activeReasonCodes);
        expectedEffectIntents = copy(expectedEffectIntents);
        candidateEffectIntents = copy(candidateEffectIntents);
        activeEffectIntents = copy(activeEffectIntents);
    }

    private static List<String> copy(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
