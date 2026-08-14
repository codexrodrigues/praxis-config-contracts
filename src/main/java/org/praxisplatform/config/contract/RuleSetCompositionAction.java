package org.praxisplatform.config.contract;

/** Server-authorized operation over one exact host-composed RuleSet candidate. */
public enum RuleSetCompositionAction {
    /** Prepare and inspect a candidate without changing the active snapshot. */
    PREPARE,
    /** Record a governed approval for the exact candidate digest. */
    APPROVE,
    /** Publish and activate the exact approved candidate digest. */
    PUBLISH
}
