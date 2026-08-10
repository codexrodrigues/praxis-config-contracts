# Praxis Config Contracts

`praxis-config-contracts` is the lightweight, framework-neutral Maven boundary
used by Java hosts that consume published Praxis configuration without embedding
the complete `praxis-config-starter` control plane.

The first contract is deliberately small:

- `PublishedRuleSnapshotHeadScope` identifies tenant, environment and RuleSet;
- `PublishedRuleSnapshotHead` separates immutable snapshot content identity from
  mutable activation identity and preserves whether the returned view came from
  publication, rollback or an active-head read;
- `PublishedRuleSnapshotHeadActivationType` closes that provenance vocabulary as
  `PUBLISHED`, `ROLLED_BACK` or `ACTIVE`;
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
  <version>0.1.0-beta.1</version>
</dependency>
```

The coordinate is not consumable until its repository and official Maven Central
release workflow exist and this version has been published. A local Maven install
is not downstream release evidence.

## Adoption order

1. Create the dedicated public repository and copy this scaffold without changing
   the `0.1.0-beta.1` contract line.
2. Add the official Java release workflow: clean Java 21 verification on `main`,
   then version/tag creation by `workflow_dispatch`, followed by Central Portal
   publication only from the accepted `v<semver>` tag. Configure Central token,
   GPG key/passphrase and the release automation token before creating a tag.
3. Publish `0.1.0-beta.1`, verify its POM, main JAR, sources, Javadocs and signatures
   in Maven Central, and build a clean isolated consumer against that public
   coordinate. Do not use `mvn install` as this proof.
4. In the next compatible `praxis-config-starter` RC, depend on the public contract,
   implement `PublishedRuleSnapshotHeadReader` from the verified active head and
   map `DomainRuleSnapshotActivationResponse.activationType` to the closed enum.
   Remove the old in-starter Java
   reader only in the same beta migration cycle; keep the HTTP response compatible.
5. Publish that Config Starter RC and migrate Quickstart runtime consumption to
   the lightweight head type. Quickstart may still depend on the full starter
   because it deliberately embeds the control plane; its proof establishes API
   compatibility and last-known-good activation.
6. Only after both public coordinates exist, let Ergon depend on contracts plus
   `praxis-rules-engine` and supply an authenticated adapter appropriate to its
   deployment topology. Prove tenant/environment/RuleSet scope, missing head,
   unavailable control plane, stale revision, rollback ETag rotation, invalid
   snapshot and last-known-good retention.

## Gate

Use Java 21 and Maven 3.9 or newer:

```powershell
mvn clean verify
```

Releases are created only through the official GitHub Actions workflow. See
[RELEASING.md](RELEASING.md) for secrets, tagging and Maven Central verification.
