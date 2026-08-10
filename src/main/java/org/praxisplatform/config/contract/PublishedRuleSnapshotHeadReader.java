package org.praxisplatform.config.contract;

import java.util.Optional;

/**
 * Minimal host port for reading an active published RuleSet head.
 *
 * <p>This contract defines neither transport nor storage. The Config control plane supplies an
 * adapter backed by its governed head; a remote host may supply an authenticated HTTP adapter.
 * Implementations must return an empty result only when the exact scope has no active head and must
 * surface unavailable, invalid or unauthorized reads as failures instead of inventing a fallback.
 */
@FunctionalInterface
public interface PublishedRuleSnapshotHeadReader {

    /**
     * Reads the active head for one exact tenant, environment and RuleSet scope.
     *
     * @param scope exact governed lookup scope
     * @return the active head, or empty only when that exact scope has no active head
     */
    Optional<PublishedRuleSnapshotHead> findActive(PublishedRuleSnapshotHeadScope scope);
}
