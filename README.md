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

## Política de CI e publicação

Validação local durante desenvolvimento; Actions apenas no fechamento necessário de versões. Consulte [ACTIONS-RELEASE-POLICY.md](ACTIONS-RELEASE-POLICY.md) para gatilhos, gates e recuperação.

## Candidato G3: identidade por operação

`PublishedRuleSnapshotHeadScope` exige as quatro coordenadas: tenant, ambiente,
família e operação. Operações da mesma família podem ter heads independentes;
o leitor não escolhe uma operação padrão quando a seleção estiver incompleta.

`RuleSetCompositionPublication` retorna somente a identidade imutável publicada:
snapshot, família, operação, versão e hash. Publicação e ativação têm contratos
distintos; ETag/revisão/tipo de ativação continuam no recibo de head. Não fabricar
um head para completar o retorno do publisher. Os consumidores devem atualizar
construtores, serialização e seleção no mesmo corte canônico, sem overload antigo.

As coordenadas `ontology-g3-operations-r3-SNAPSHOT` são exclusivas da prova local.
O teste consumidor usa o Engine atual com contrato 1.7; publicação e adoção pública
continuam sujeitas ao workflow e aos gates oficiais.

## Candidato G3: conjunto autoritativo de heads

`PublishedRuleSnapshotHeadSetScope` exige tenant, ambiente, família e o conjunto exato de operações (1–64, sem duplicatas). `PublishedRuleSnapshotHeadSetReader` devolve uma única fotografia de storage, nunca um laço de leituras individuais. Ausência de qualquer operação devolve seleção ausente; falhas, corrupção e acesso recusado não equivalem a ausência.

`PublishedRuleSnapshotHeadSet` valida todas as coordenadas, o estado ACTIVE e a propriedade comum da família. Seu fingerprint inclui hash do conteúdo, snapshot, ETag e revisão de ativação de cada head, detectando também A→B→A. Não constitui uma aprovação adicional nem um lock que atravesse bancos. O host continua responsável por vigência atual, contrato tipado, produto protegido e instalação atômica do conjunto completo. A porta de leitura individual mantém sua finalidade própria e não implementa essa garantia de conjunto.
