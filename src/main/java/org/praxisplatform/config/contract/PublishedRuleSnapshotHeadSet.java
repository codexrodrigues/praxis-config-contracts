package org.praxisplatform.config.contract;

import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.praxisplatform.rules.digest.PraxisCanonicalJson;

/** Complete immutable selection returned by one consistent storage read, never nine individual reads. */
public record PublishedRuleSnapshotHeadSet(
        PublishedRuleSnapshotHeadSetScope scope, Map<String, PublishedRuleSnapshotHead> heads) {
    /** Rejects incomplete, mixed-scope or mixed-family selections before they reach a host. */
    public PublishedRuleSnapshotHeadSet {
        Objects.requireNonNull(scope, "scope is required");
        heads = Map.copyOf(heads);
        if (!heads.keySet().equals(Set.copyOf(scope.operationKeys())))
            throw new IllegalArgumentException("Every requested operation must have exactly one head");
        var identities = new java.util.HashSet<java.util.List<String>>();
        for (var entry : heads.entrySet()) {
            var head = entry.getValue();
            var snapshot = head.snapshot();
            var ref = snapshot.ruleSet().ref();
            if (!snapshot.tenantId().equals(scope.tenantId()) || !snapshot.environment().equals(scope.environment())
                    || !ref.ruleSetKey().equals(scope.ruleSetKey()) || !ref.operationKey().equals(entry.getKey())
                    || head.activationType() != PublishedRuleSnapshotHeadActivationType.ACTIVE)
                throw new IllegalArgumentException("Head-set scope or current activation does not match");
            identities.add(java.util.List.of(snapshot.ownerServiceKey(), ref.domainKey(), ref.boundedContextKey()));
        }
        if (identities.size() != 1) throw new IllegalArgumentException("Head-set family ownership must be consistent");
    }

    /** Includes activation identity so reselecting the same content (ABA) changes the evidence. */
    public String fingerprint() {
        var root = new JsonMapper().createObjectNode();
        root.put("tenant", scope.tenantId()).put("environment", scope.environment()).put("family", scope.ruleSetKey());
        var members = root.putObject("heads");
        for (String operation : scope.operationKeys()) {
            var head = heads.get(operation);
            members.putObject(operation).put("snapshot", head.snapshot().snapshotKey())
                    .put("content", head.snapshotContentHash()).put("etag", head.headEtag())
                    .put("revision", head.activationRevision());
        }
        return PraxisCanonicalJson.sha256(root);
    }
}
