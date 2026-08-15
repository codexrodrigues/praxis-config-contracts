package org.praxisplatform.config.contract;

import java.time.Instant;

/**
 * Safe provenance of the authority used to establish scenario expectations.
 *
 * @param authorityType canonical authority type
 * @param artifactRef safe reference to the evidence artifact
 * @param artifactDigest SHA-256 of that artifact
 * @param observedAtUtc instant at which the evidence was observed
 * @param eligibility ELIGIBLE, INELIGIBLE or PENDING governance state
 */
public record DomainRuleTestBaselineEvidence(
        String authorityType,
        String artifactRef,
        String artifactDigest,
        Instant observedAtUtc,
        String eligibility) {}
