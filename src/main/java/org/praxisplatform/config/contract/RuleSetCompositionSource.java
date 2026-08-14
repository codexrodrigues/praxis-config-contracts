package org.praxisplatform.config.contract;

import java.util.Objects;
import java.util.UUID;

/**
 * Safe governed source identity; executable conditions are deliberately absent.
 *
 * @param definitionId immutable governed definition identity
 * @param ruleKey stable semantic rule identity
 * @param version positive governed definition version
 * @param status server-owned lifecycle status
 */
public record RuleSetCompositionSource(
        UUID definitionId,
        String ruleKey,
        int version,
        String status) {

    /** Validates and normalizes the governed source identity. */
    public RuleSetCompositionSource {
        definitionId = Objects.requireNonNull(definitionId, "definitionId is required");
        ruleKey = required(ruleKey, "ruleKey");
        if (version < 1) throw new IllegalArgumentException("version must be positive");
        status = required(status, "status");
    }

    private static String required(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value.trim();
    }
}
