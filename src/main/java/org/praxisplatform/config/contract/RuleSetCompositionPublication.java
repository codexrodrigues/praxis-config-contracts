package org.praxisplatform.config.contract;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Safe activation receipt; immutable executable snapshot content remains private.
 *
 * @param snapshotKey immutable published snapshot identity
 * @param ruleSetKey stable host-owned RuleSet identity
 * @param ruleSetVersion positive composed RuleSet version
 * @param snapshotContentHash uppercase SHA-256 over immutable executable content
 * @param headEtag strong ETag for subsequent conditional mutations
 * @param activationRevision positive monotonic activation revision
 * @param activationType server-owned activation classification
 */
public record RuleSetCompositionPublication(
        String snapshotKey,
        String ruleSetKey,
        int ruleSetVersion,
        String snapshotContentHash,
        String headEtag,
        long activationRevision,
        PublishedRuleSnapshotHeadActivationType activationType) {

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
        headEtag = required(headEtag, "headEtag");
        if (activationRevision < 1) throw new IllegalArgumentException("activationRevision must be positive");
        activationType = Objects.requireNonNull(activationType, "activationType is required");
    }

    private static String required(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value.trim();
    }
}
