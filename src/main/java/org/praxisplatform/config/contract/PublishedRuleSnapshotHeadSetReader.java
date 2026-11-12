package org.praxisplatform.config.contract;

import java.util.Optional;

/**
 * Framework-neutral port for one complete, consistent selection of active operation heads.
 * Implementations must capture all members in one storage snapshot, without looping over a
 * single-head reader. Empty means at least one requested operation has no selected head;
 * corruption, authorization failure or unavailability must surface as failures. This read
 * grants neither domain authorization nor instantaneous cross-database revocation.
 */
@FunctionalInterface
public interface PublishedRuleSnapshotHeadSetReader {
    /** Reads current authority for exactly the requested operation set; partial bundles are forbidden. */
    Optional<PublishedRuleSnapshotHeadSet> findActive(PublishedRuleSnapshotHeadSetScope scope);
}
