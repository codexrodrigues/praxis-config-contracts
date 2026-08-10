package org.praxisplatform.config.contract;

import java.util.Objects;
import java.util.regex.Pattern;
import org.praxisplatform.rules.contract.PublishedRuleSnapshot;

/**
 * Host-facing view of the mutable head that selects one immutable published snapshot.
 *
 * <p>The snapshot and its content hash identify immutable rule content. The opaque head ETag and
 * monotonic activation revision identify the mutable selection and therefore change on publication
 * and rollback, including rollback to content seen previously.</p>
 *
 * @param snapshot immutable runtime-neutral snapshot selected by the head
 * @param snapshotContentHash canonical uppercase SHA-256 of the immutable snapshot content
 * @param headEtag opaque strong identity of this head activation
 * @param activationRevision monotonic activation revision, including rollbacks
 * @param activationType control-plane operation provenance for this returned view
 */
public record PublishedRuleSnapshotHead(
        PublishedRuleSnapshot snapshot,
        String snapshotContentHash,
        String headEtag,
        long activationRevision,
        PublishedRuleSnapshotHeadActivationType activationType) {

    private static final Pattern SHA_256 = Pattern.compile("[0-9A-F]{64}");

    /** Validates the immutable content identity and mutable activation identity. */
    public PublishedRuleSnapshotHead {
        snapshot = Objects.requireNonNull(snapshot, "snapshot is required");
        snapshotContentHash = required(snapshotContentHash, "snapshotContentHash");
        if (!SHA_256.matcher(snapshotContentHash).matches()) {
            throw new IllegalArgumentException("snapshotContentHash must be an uppercase SHA-256");
        }
        headEtag = required(headEtag, "headEtag");
        if (activationRevision < 1) {
            throw new IllegalArgumentException("activationRevision must be positive");
        }
        activationType = Objects.requireNonNull(activationType, "activationType is required");
    }

    private static String required(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
