package org.praxisplatform.config.contract;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Mutation command bound to the exact digest returned by candidate preparation.
 *
 * @param promotedDefinitionId promoted governed definition that anchors composition
 * @param validFromUtc inclusive UTC validity boundary
 * @param validUntilUtc optional exclusive UTC validity boundary
 * @param expectedCompositionDigest uppercase SHA-256 returned by candidate preparation
 */
public record RuleSetCompositionCandidateCommand(
        UUID promotedDefinitionId,
        String validFromUtc,
        String validUntilUtc,
        String expectedCompositionDigest) {

    private static final Pattern SHA_256 = Pattern.compile("[0-9A-F]{64}");

    /** Validates the digest-bound mutation command. */
    public RuleSetCompositionCandidateCommand {
        var request = new RuleSetCompositionCandidateRequest(
                promotedDefinitionId, validFromUtc, validUntilUtc);
        promotedDefinitionId = request.promotedDefinitionId();
        validFromUtc = request.validFromUtc();
        validUntilUtc = request.validUntilUtc();
        expectedCompositionDigest = Objects.requireNonNull(
                expectedCompositionDigest, "expectedCompositionDigest is required").trim();
        if (!SHA_256.matcher(expectedCompositionDigest).matches()) {
            throw new IllegalArgumentException("expectedCompositionDigest must be an uppercase SHA-256");
        }
    }

    /**
     * Converts the shared fields into a preparation request.
     *
     * @return the immutable preparation request represented by this command
     */
    public RuleSetCompositionCandidateRequest candidateRequest() {
        return new RuleSetCompositionCandidateRequest(
                promotedDefinitionId, validFromUtc, validUntilUtc);
    }
}
