# Praxis Config Contracts

`praxis-config-contracts` is the lightweight, framework-neutral Maven boundary
used by Java hosts that consume published Praxis configuration without embedding
the complete `praxis-config-starter` control plane.

The first contract is deliberately small:

- `PublishedRuleSnapshotHeadScope` identifies tenant, environment and RuleSet;
- `PublishedRuleSnapshotHead` separates immutable snapshot content identity from
  mutable activation identity and preserves whether the returned view came from
  publication, explicit activation, rollback or an active-head read;
- `PublishedRuleSnapshotHeadActivationType` closes that provenance vocabulary as
  `PUBLISHED`, `ACTIVATED`, `ROLLED_BACK` or `ACTIVE`;
- `PublishedRuleSnapshotHeadReader` is the read port implemented by a governed
  Config adapter or by an authenticated remote adapter owned by the host.

This artifact owns no persistence, authoring, approval, publication, rollback,
HTTP client, Spring auto-configuration or controller. `praxis-config-starter`
continues to own those control-plane concerns. `praxis-rules-engine` continues to
own `PublishedRuleSnapshot` and deterministic compilation/evaluation.

## Dependency

```xml
<dependency>
  <groupId>io.github.codexrodrigues</groupId>
  <artifactId>praxis-config-contracts</artifactId>
  <version>0.1.0-beta.2</version>
</dependency>
```

Version `0.1.0-beta.2` adds explicit activation provenance for selecting a newer
immutable publication. It remains compatible with the beta.1 scope, head and
reader contracts. A local Maven install is not downstream release evidence.

## Beta.2 adoption order

1. Verify and publish `0.1.0-beta.2` through the official tag workflow.
2. Update the next `praxis-config-starter` RC to the public beta.2 coordinate and
   expose explicit newer-version activation as `ACTIVATED` while preserving
   `ROLLED_BACK` for selection of an older publication.
3. Update Quickstart from the public Config Starter coordinate and prove both
   operator paths with strong `If-Match` and authenticated scope.
4. Ergon continues to consume `GET .../head`, whose returned activation type is
   `ACTIVE`; no host adapter migration is required for this additive provenance
   value. Prove ETag rotation and last-known-good after downstream publication.

## Gate

Use Java 21 and Maven 3.9 or newer:

```powershell
mvn clean verify
```

Releases are created only through the official GitHub Actions workflow. See
[RELEASING.md](RELEASING.md) for secrets, tagging and Maven Central verification.
