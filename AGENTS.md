# AGENTS.md — Praxis Config Contracts

## Scope

These instructions apply to the complete `praxis-config-contracts` repository.

## Canonical boundary

- This artifact owns only framework-neutral contracts used by hosts to consume published Praxis configuration.
- `praxis-config-starter` owns persistence, authoring, approval, publication, rollback, ETag lifecycle and control-plane adapters.
- `praxis-rules-engine` owns immutable snapshots and deterministic compilation/evaluation.
- Do not introduce Spring, Spring Boot, JPA, HTTP clients, controllers, persistence or auto-configuration here.
- Do not re-export the complete API of another public Praxis artifact.

## Public contract changes

- Classify changes to exported Java types as `contrato-publico` and document consumer impact before editing.
- Keep scope, immutable snapshot identity, mutable activation identity and activation provenance explicit.
- Missing, stale, invalid or cross-scope heads must remain distinguishable to the consuming adapter; do not add silent fallback semantics.
- While the artifact remains beta, update the canonical contract and its consumers in the same release cycle instead of adding parallel compatibility types.

## Validation and release

- Use Java 21 and Maven 3.9 or newer.
- Minimum gate: `mvn -B clean verify`.
- Confirm the runtime dependency tree remains free of Spring, Spring Boot and JPA.
- Publication is performed only by the official GitHub Actions workflow from a `v<semver>` tag created by workflow dispatch.
- Never use local `mvn install` as downstream release evidence and never publish with local `mvn deploy`.

## Economia de GitHub Actions

O padrão durante desenvolvimento é zero execuções remotas. Valide localmente o escopo alterado; commits, PRs, documentação interna e conclusão de tarefas não são motivos para iniciar Actions. Use os workflows manuais somente no fechamento autorizado de uma versão/publicação ou na prova necessária do host já implantado. Não use `[skip ci]` como mecanismo principal nem desabilite checks/proteções para economizar.

Antes de push, tag ou dispatch, confira os gatilhos reais de `.github/workflows/`. Tags de release publicam artefatos: não criá-las para testar a automação. Diagnostique localmente antes de repetir um job; conserve a evidência da revisão e dos artefatos usados. Monitores operacionais explicitamente mantidos são independentes do CI de commits. Consulte [ACTIONS-RELEASE-POLICY.md](ACTIONS-RELEASE-POLICY.md) para os pontos de entrada e recuperação.
