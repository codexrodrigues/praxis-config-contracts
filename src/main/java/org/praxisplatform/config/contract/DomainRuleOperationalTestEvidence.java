package org.praxisplatform.config.contract;

/**
 * Sanitized host evidence for a state-changing CREATE or UPDATE policy scenario.
 *
 * @param operationMode governed CREATE or UPDATE operation
 * @param beforeStateDigest digest of the redacted state before execution, when applicable
 * @param afterStateDigest digest of the redacted state after execution, when mutation occurred
 * @param mutationObserved whether the host observed a mutation
 * @param noMutationVerified whether the host proved that no mutation occurred
 * @param cleanupVerified whether the test fixture was restored or removed
 * @param effectLedgerDigest digest of the redacted effect ledger, when available
 * @param baselineCallCount measured calls to the declared baseline authority
 */
public record DomainRuleOperationalTestEvidence(
        String operationMode,
        String beforeStateDigest,
        String afterStateDigest,
        boolean mutationObserved,
        boolean noMutationVerified,
        boolean cleanupVerified,
        String effectLedgerDigest,
        int baselineCallCount) {}
