package org.praxisplatform.config.contract;

import java.util.List;
import java.util.TreeSet;

/** Exact bounded selection of operations whose heads must be read in one storage snapshot. */
public record PublishedRuleSnapshotHeadSetScope(
        String tenantId, String environment, String ruleSetKey, List<String> operationKeys) {
    /** Maximum number of operations in one consistent control-plane read. */
    public static final int MAX_OPERATIONS = 64;

    /** Normalizes coordinates and rejects empty, duplicate or oversized operation selections. */
    public PublishedRuleSnapshotHeadSetScope {
        tenantId = required(tenantId, 128);
        environment = required(environment, 128);
        ruleSetKey = required(ruleSetKey, 512);
        if (operationKeys == null || operationKeys.isEmpty() || operationKeys.size() > MAX_OPERATIONS)
            throw new IllegalArgumentException("A bounded nonempty exact operation selection is required");
        var unique = new TreeSet<String>();
        for (String operation : operationKeys) {
            if (!unique.add(required(operation, 512)))
                throw new IllegalArgumentException("Operation selection must not contain duplicates");
        }
        operationKeys = List.copyOf(unique);
    }

    private static String required(String value, int maximum) {
        if (value == null || value.isBlank() || value.trim().length() > maximum)
            throw new IllegalArgumentException("Invalid governed head-set coordinate");
        return value.trim();
    }
}
