package org.praxisplatform.config.contract;

/** Provenance of the control-plane operation that produced the returned head view. */
public enum PublishedRuleSnapshotHeadActivationType {
    /** Historical provenance from publication that also selected a head; new publication is not activation. */
    PUBLISHED,
    /** Returned directly when a newer immutable publication is selected explicitly. */
    ACTIVATED,
    /** Returned directly by a rollback-by-selection operation. */
    ROLLED_BACK,
    /** Returned by a read of the currently active head. */
    ACTIVE
}
