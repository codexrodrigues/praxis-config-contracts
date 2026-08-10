package org.praxisplatform.config.contract;

import java.util.Objects;

/**
 * Exact control-plane scope of one active published RuleSet head.
 *
 * @param tenantId governed tenant identity
 * @param environment governed environment identity
 * @param ruleSetKey stable RuleSet identity
 */
public record PublishedRuleSnapshotHeadScope(
        String tenantId,
        String environment,
        String ruleSetKey) {

    /** Normalizes the three required scope coordinates. */
    public PublishedRuleSnapshotHeadScope {
        tenantId = required(tenantId, "tenantId");
        environment = required(environment, "environment");
        ruleSetKey = required(ruleSetKey, "ruleSetKey");
    }

    private static String required(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
