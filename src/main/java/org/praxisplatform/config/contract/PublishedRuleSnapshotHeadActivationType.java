package org.praxisplatform.config.contract;

/** Provenance of the control-plane operation that produced the returned head view. */
public enum PublishedRuleSnapshotHeadActivationType {
    /** Returned directly by a successful publication operation. */
    PUBLISHED,
    /** Returned directly when a newer immutable publication is selected explicitly. */
    ACTIVATED,
    /** Returned directly by a rollback-by-selection operation. */
    ROLLED_BACK,
    /** Returned by a read of the currently active head. */
    ACTIVE
}
