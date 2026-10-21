package org.praxisplatform.config.contract;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Safe publication-only receipt; immutable executable snapshot content remains private.
 *
 * <p>Publication makes this immutable identity available for governed review and preload. It does
 * not select an active head or confer execution readiness. Mutable activation identity belongs
 * exclusively to {@link PublishedRuleSnapshotHead} after explicit selection.</p>
 *
 * @param snapshotKey immutable published snapshot identity
 * @param ruleSetKey stable host-owned RuleSet identity
 * @param ruleSetVersion positive composed RuleSet version
 * @param snapshotContentHash uppercase SHA-256 over immutable executable content
 */
public record RuleSetCompositionPublication(
        String snapshotKey,
        String ruleSetKey,
        int ruleSetVersion,
        String snapshotContentHash) {

    private static final Pattern SHA_256 = Pattern.compile("[0-9A-F]{64}");

    /** Validates and normalizes the safe publication receipt. */
    public RuleSetCompositionPublication {
        snapshotKey = required(snapshotKey, "snapshotKey");
        ruleSetKey = required(ruleSetKey, "ruleSetKey");
        if (ruleSetVersion < 1) throw new IllegalArgumentException("ruleSetVersion must be positive");
        snapshotContentHash = required(snapshotContentHash, "snapshotContentHash");
        if (!SHA_256.matcher(snapshotContentHash).matches()) {
            throw new IllegalArgumentException("snapshotContentHash must be an uppercase SHA-256");
        }
    }

    private static String required(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value.trim();
    }
}
