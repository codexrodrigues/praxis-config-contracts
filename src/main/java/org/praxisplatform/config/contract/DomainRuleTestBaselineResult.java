package org.praxisplatform.config.contract;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Independent, redacted result returned by the declared baseline authority for one scenario.
 *
 * <p>Raw Oracle rows, runtime facts, SQL, exception messages and execution traces are deliberately
 * absent. A host may publish only bounded reason/effect codes plus digests of its plan and redacted
 * trace.</p>
 *
 * @param decision canonical five-state baseline decision
 * @param output redacted baseline output
 * @param reasonCodes bounded semantic reason codes
 * @param effectIntents bounded semantic effect intents
 * @param planDigest SHA-256 of the baseline plan
 * @param traceDigest SHA-256 of a redacted trace, when available
 * @param errorCode stable error code for TECHNICAL_ERROR only
 */
public record DomainRuleTestBaselineResult(
        String decision,
        JsonNode output,
        List<String> reasonCodes,
        List<String> effectIntents,
        String planDigest,
        String traceDigest,
        String errorCode) {

    /** Defensively copies the bounded assertion collections. */
    public DomainRuleTestBaselineResult {
        reasonCodes = reasonCodes == null ? List.of() : List.copyOf(reasonCodes);
        effectIntents = effectIntents == null ? List.of() : List.copyOf(effectIntents);
    }
}
