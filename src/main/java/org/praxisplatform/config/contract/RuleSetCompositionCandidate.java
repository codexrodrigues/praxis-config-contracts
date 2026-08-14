package org.praxisplatform.config.contract;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Safe review projection of a complete host-composed RuleSet candidate.
 *
 * @param ruleSetKey stable host-owned RuleSet identity
 * @param ruleSetVersion positive composed RuleSet version
 * @param compositionDigest uppercase SHA-256 over the exact source composition
 * @param implementationCatalogDigest uppercase SHA-256 over the compatible implementation catalog
 * @param currentHeadEtag optional strong ETag of the currently active head
 * @param sources immutable governed source identities, without executable conditions
 * @param authorizedActions distinct server-authorized actions for the current principal; may be empty
 */
public record RuleSetCompositionCandidate(
        String ruleSetKey,
        int ruleSetVersion,
        String compositionDigest,
        String implementationCatalogDigest,
        String currentHeadEtag,
        List<RuleSetCompositionSource> sources,
        List<RuleSetCompositionAction> authorizedActions) {

    private static final Pattern SHA_256 = Pattern.compile("[0-9A-F]{64}");

    /** Validates and defensively copies the safe candidate projection. */
    public RuleSetCompositionCandidate {
        ruleSetKey = required(ruleSetKey, "ruleSetKey");
        if (ruleSetVersion < 1) throw new IllegalArgumentException("ruleSetVersion must be positive");
        compositionDigest = digest(compositionDigest, "compositionDigest");
        implementationCatalogDigest = digest(implementationCatalogDigest, "implementationCatalogDigest");
        currentHeadEtag = currentHeadEtag == null || currentHeadEtag.isBlank() ? null : currentHeadEtag.trim();
        sources = List.copyOf(Objects.requireNonNull(sources, "sources are required"));
        if (sources.isEmpty()) throw new IllegalArgumentException("sources must not be empty");
        authorizedActions = List.copyOf(Objects.requireNonNull(authorizedActions, "authorizedActions are required"));
        if (authorizedActions.stream().distinct().count() != authorizedActions.size()) {
            throw new IllegalArgumentException("authorizedActions must be distinct");
        }
    }

    private static String required(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value.trim();
    }

    private static String digest(String value, String field) {
        String normalized = required(value, field);
        if (!SHA_256.matcher(normalized).matches()) {
            throw new IllegalArgumentException(field + " must be an uppercase SHA-256");
        }
        return normalized;
    }
}
