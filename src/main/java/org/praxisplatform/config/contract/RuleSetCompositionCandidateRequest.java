package org.praxisplatform.config.contract;

import java.util.Objects;
import java.util.UUID;

/**
 * Minimal host-neutral request for preparing a complete RuleSet candidate.
 *
 * @param promotedDefinitionId promoted governed definition that anchors composition
 * @param validFromUtc inclusive UTC validity boundary
 * @param validUntilUtc optional exclusive UTC validity boundary
 */
public record RuleSetCompositionCandidateRequest(
        UUID promotedDefinitionId,
        String validFromUtc,
        String validUntilUtc) {

    /** Validates and normalizes the candidate boundaries. */
    public RuleSetCompositionCandidateRequest {
        promotedDefinitionId = Objects.requireNonNull(promotedDefinitionId, "promotedDefinitionId is required");
        validFromUtc = required(validFromUtc, "validFromUtc");
        validUntilUtc = optional(validUntilUtc);
    }

    private static String required(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value.trim();
    }

    private static String optional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
