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
- `DomainRuleTestRunRecordRequest` and its result/evidence records are the
  framework-neutral transport vocabulary used by runtime hosts to submit
  idempotent, redacted candidate/active/baseline evidence to the Config control
  plane without importing Starter persistence or auto-configuration.

This artifact owns no persistence, authoring, approval, publication, rollback,
HTTP client, Spring auto-configuration or controller. `praxis-config-starter`
continues to own those control-plane concerns. `praxis-rules-engine` continues to
own `PublishedRuleSnapshot` and deterministic compilation/evaluation.

## Dependency

```xml
<dependency>
  <groupId>io.github.codexrodrigues</groupId>
  <artifactId>praxis-config-contracts</artifactId>
  <version>0.1.0-beta.4-SNAPSHOT</version>
</dependency>
```

Version `0.1.0-beta.4` adds the host-neutral Policy Studio Test Run transport:
an explicit idempotency key, one independent redacted baseline lane per
scenario, sanitized operational evidence and the corresponding receipt. It does
not add a recorder implementation, HTTP client, controller or persistence.
Those remain owned by the Config control plane and each authenticated host
adapter. A local Maven install is not downstream release evidence.

## Beta.4 adoption order

1. Verify and publish `0.1.0-beta.4` through the official tag workflow.
2. Migrate Config Starter and Quickstart from Starter-owned Test Run DTOs to
   these records without creating compatibility aliases.
3. Keep scenario execution, baseline observation and redaction host-owned;
   Config owns validation, idempotency, persistence and stage gates.
4. Implement the Ergon transport adapter against this artifact only after its
   domain-specific admission gate; never import Config Starter into the host.

## Gate

Use Java 21 and Maven 3.9 or newer:

```powershell
mvn clean verify
```

Releases are created only through the official GitHub Actions workflow. See
[RELEASING.md](RELEASING.md) for secrets, tagging and Maven Central verification.
