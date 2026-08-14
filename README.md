# Praxis Config Contracts

`praxis-config-contracts` is the lightweight, framework-neutral Maven boundary
used by Java hosts that consume published Praxis configuration without embedding
the complete `praxis-config-starter` control plane.

The contracts are deliberately small:

- `PublishedRuleSnapshotHeadScope` identifies tenant, environment and RuleSet;
- `PublishedRuleSnapshotHead` separates immutable snapshot content identity from
  mutable activation identity and preserves whether the returned view came from
  publication, explicit activation, rollback or an active-head read;
- `PublishedRuleSnapshotHeadActivationType` closes that provenance vocabulary as
  `PUBLISHED`, `ACTIVATED`, `ROLLED_BACK` or `ACTIVE`;
- `PublishedRuleSnapshotHeadReader` is the read port implemented by a governed
  Config adapter or by an authenticated remote adapter owned by the host.
- `RuleSetCompositionCandidateRequest` and `RuleSetCompositionCandidateCommand`
  identify an exact promoted definition, validity window and inspected digest;
- `RuleSetCompositionCandidate`, `RuleSetCompositionSource` and
  `RuleSetCompositionAction` expose only safe provenance and server-authorized
  operations, never the executable RuleSet graph;
- `RuleSetCompositionPublication` is a redacted activation receipt that keeps
  immutable content identity separate from the mutable head identity.

This artifact owns no persistence, authoring, approval, publication, rollback,
HTTP client, Spring auto-configuration or controller. `praxis-config-starter`
continues to own those control-plane concerns. `praxis-rules-engine` continues to
own `PublishedRuleSnapshot` and deterministic compilation/evaluation.

## Dependency

```xml
<dependency>
  <groupId>io.github.codexrodrigues</groupId>
  <artifactId>praxis-config-contracts</artifactId>
  <version>0.1.0-beta.3</version>
</dependency>
```

Version `0.1.0-beta.3` adds the host-neutral Policy Studio composition command
and safe-view vocabulary. It does not add a compositor, HTTP client, controller
or persistence. Beta.2 head contracts remain unchanged. A local Maven install
is not downstream release evidence.

## Beta.3 adoption order

1. Verify and publish `0.1.0-beta.3` through the official tag workflow.
2. Migrate the Quickstart host compositor from its private DTOs to these records
   without changing its HTTP JSON shape.
3. Keep composition logic host-owned and snapshot lifecycle Config-owned.
4. Implement a second host compositor only after its domain-specific admission
   gate; conformance must cover digest, ETag, actions and scoped identities.

## Gate

Use Java 21 and Maven 3.9 or newer:

```powershell
mvn clean verify
```

Releases are created only through the official GitHub Actions workflow. See
[RELEASING.md](RELEASING.md) for secrets, tagging and Maven Central verification.
