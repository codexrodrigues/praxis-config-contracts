# Releasing — Praxis Config Contracts

## Public line

The active public line is `0.1.0-beta.*`. Compatible corrections remain on this line while the contract is validated by `praxis-config-starter`, Quickstart and Ergon. A stable release requires verified downstream consumption; a new major requires a governed breaking-change plan.

## Required repository secrets

- `CENTRAL_TOKEN_USER`
- `CENTRAL_TOKEN_PASS`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`
- `GPG_KEY_ID` (optional)
- `RELEASE_PAT`, with permission to push repository contents

`RELEASE_PAT` is required because the workflow-created tag must trigger the separate tag publication run.

## Release flow

1. Confirm the intended commit is on remote `main` and CI is green.
2. Run locally with Java 21:

   ```powershell
   mvn -B clean verify
   ```

3. In GitHub Actions, run **Release Praxis Config Contracts** on `main` with `create_tag=true` and either an explicit version or `bump`/`preid`.
4. The workflow persists the resolved version in `pom.xml`, creates the annotated `v<semver>` tag and pushes both atomically.
5. The tag run verifies the tagged version, signs the POM/JAR/sources/Javadocs and publishes through the Central Portal.
6. Wait for the Maven Central availability probe to pass.
7. Build an isolated consumer against the public coordinate. A local Maven install is not release evidence.

Do not create release tags manually, run `mvn deploy` locally or bypass a failed CI/release gate.
