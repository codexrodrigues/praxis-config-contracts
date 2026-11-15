# Praxis Config Contracts

`praxis-config-contracts` é a fronteira Maven leve e neutra em relação a frameworks usada por hosts Java da [**Praxis Platform**](https://praxisui.dev/) que consomem configurações e cabeças publicadas sem precisar embarcar o plano de controle completo de [`praxis-config-starter`](https://github.com/codexrodrigues/praxis-config-starter).

## Papel na Solução Praxis, Diferencial e Ganho em Tokens de IA

Na arquitetura **Descreva Uma Vez, Opere em Todo Lugar** da plataforma, regras de negócio (`decisionKind = BUSINESS`, via [`praxis-rules-engine`](https://github.com/codexrodrigues/praxis-rules-engine)), políticas de autorização e proteção de dados (`decisionKind = AUTHORIZATION`, via [`praxis-authz-contracts`](https://github.com/codexrodrigues/praxis-authz-contracts), [`praxis-authz-enforcement-core`](https://github.com/codexrodrigues/praxis-authz-enforcement-core), [`praxis-authz-opa`](https://github.com/codexrodrigues/praxis-authz-opa) e [`praxis-authz-starter`](https://github.com/codexrodrigues/praxis-authz-starter)) e metadados semânticos ([`praxis-metadata-starter`](https://github.com/codexrodrigues/praxis-metadata-starter)) convergem no mesmo PAP governado no [`praxis-policy-studio`](https://github.com/codexrodrigues/praxis-policy-studio).

- **Problema corporativo resolvido:** Evita que microserviços consumidores ou gateways tenham de importar bancos de dados de configuração, WebClients internos ou auto-configuração completa do Control Plane apenas para ler qual cabeça (`PublishedRuleSnapshotHead`) está ativa ou enviar evidências redigidas de execução;
- **À frente do mercado e economia de tokens para agentes:** Contratos enxutos, explícitos e livres de dependências Spring permitem que ferramentas, gateways, runtimes sem container e LLMs leiam e integrem o protocolo de ativação/evidência com mínimo contexto em memória, separando identidade imutável de conteúdo (`snapshotKey`, `snapshotContentHash`) da identidade mutável de ativação.

Os contratos são deliberadamente enxutos:

- `PublishedRuleSnapshotHeadScope` identifica tenant, ambiente e `RuleSet`;
- `PublishedRuleSnapshotHead` separa a identidade imutável do conteúdo do snapshot da identidade mutável de ativação e preserva ativação explícita, rollback ou leitura de cabeça ativa, incluindo proveniência histórica retida;
- `PublishedRuleSnapshotHeadActivationType` fecha esse vocabulário de proveniência como `ACTIVATED`, `ROLLED_BACK` ou `ACTIVE` (`PUBLISHED` permanece apenas como valor histórico de publicação anterior que também selecionava uma cabeça);
- `PublishedRuleSnapshotHeadReader` é a porta de leitura implementada por um adaptador governado de Config ou por um adaptador remoto autenticado pertencente ao host;
- `RuleSetCompositionCandidateRequest` e `RuleSetCompositionCandidateCommand` identificam exatamente uma definição promovida, janela de validade e digest inspecionado;
- `RuleSetCompositionCandidate`, `RuleSetCompositionSource` e `RuleSetCompositionAction` expõem apenas proveniência segura e operações autorizadas pelo servidor, nunca o grafo executável completo do `RuleSet`;
- `RuleSetCompositionPublication` é um recibo redigido apenas de publicação contendo `snapshotKey`, `ruleSetKey`, `ruleSetVersion` e `snapshotContentHash` (não carrega `ETag` de cabeça, revisão de ativação nem concessão de prontidão de execução — a ativação explícita produz o contrato de cabeça separado);
- `DomainRuleTestRunRecordRequest` e seus registros de resultado/evidência formam o vocabulário neutro de transporte usado pelos hosts em runtime para submeter evidências idempotentes e redigidas de candidato/ativo/baseline ao plano de controle Config sem importar persistência nem auto-configuração do Starter.

Este artefato **não** possui persistência, autoria, aprovação, publicação, rollback, cliente HTTP, auto-configuração Spring nem controllers. [`praxis-config-starter`](https://github.com/codexrodrigues/praxis-config-starter) continua sendo o dono do plano de controle (PAP), enquanto [`praxis-rules-engine`](https://github.com/codexrodrigues/praxis-rules-engine) governa `PublishedRuleSnapshot` (`decisionKind = BUSINESS`) e a família `praxis-authz-*` governa autorização/escopo/mascaramento (`decisionKind = AUTHORIZATION`).

## Dependência Maven

```xml
<dependency>
  <groupId>io.github.codexrodrigues</groupId>
  <artifactId>praxis-config-contracts</artifactId>
  <version>${praxis.config.contracts.version}</version>
</dependency>
```

Set `praxis.config.contracts.version` to the exact officially published release
containing the required contract. The G2 publication-only change below is source
until its official release; the earlier `0.1.0-beta.4` does not provide it.

## Publication-only migration (G2)

O código atual depende de `praxis-rules-engine` `0.1.0-beta.16` (contrato de engine `1.5`). `RuleSetCompositionPublication` mantém os quatro campos imutáveis de identidade já existentes e remove `headEtag`, `activationRevision` e `activationType`. Trata-se de uma *breaking change* deliberada da fase beta, sem recibo paralelo, placeholders anuláveis de ativação nem modo de compatibilidade.

Um adaptador de publicação do Config mapeia essas identidades a partir do snapshot armazenado aprovado e do seu hash canônico de conteúdo. A publicação por si só não cria uma cabeça ativa (*head*); o host pode revisar ou pré-carregar o candidato imutável antes da ativação governada separada. O hash de conteúdo nunca deve substituir uma `ETag` mutável de cabeça. `PublishedRuleSnapshotHead` continua exigindo seleção real não nula, revisão de ativação positiva e proveniência do plano de controle; a proveniência histórica `PUBLISHED` é preservada sem conceder nova autoridade de execução.

Publique oficialmente o artefato revisado de `praxis-config-contracts` antes de adotar sua coordenada exata nos consumidores de Config e hosts. Atualize chamadas de construtor e mapeamentos HTTP no mesmo ciclo: publicação devolve o recibo imutável, enquanto ativação/rollback devolvem a visão separada de cabeça. Em seguida, comprove os builds consumidores usando dependências publicadas; um `mvn install` local não fecha esse gate.

## Contrato Anterior de Test Run

A versão `0.1.0-beta.4` adiciona o transporte neutro de host para o *Test Run* do Policy Studio: uma chave de idempotência explícita, uma trilha independente de baseline redigido por cenário, evidência operacional sanitizada e o recibo correspondente. Este pacote não inclui implementação de gravador (*recorder*), cliente HTTP, controller ou persistência — essas responsabilidades continuam no plano de controle [`praxis-config-starter`](https://github.com/codexrodrigues/praxis-config-starter) e em cada adaptador autenticado de host. Um install Maven local não constitui evidência de release downstream.

## Ordem de Adoção da `beta.4`

1. Verifique e publique `0.1.0-beta.4` exclusivamente pelo workflow oficial de tag.
2. Migre `praxis-config-starter` e [`praxis-api-quickstart`](https://github.com/codexrodrigues/praxis-api-quickstart) dos DTOs de Test Run pertencentes ao Starter para estes registros sem criar aliases de compatibilidade.
3. Mantenha a execução de cenários, observação de baseline e redação sob domínio do host; Config responde pela validação, idempotência, persistência e gates de estágio.
4. Implemente o adaptador de transporte de domínio contra este artefato apenas após seu gate específico de admissão; nunca importe `praxis-config-starter` dentro de um host consumidor.

## Gate de Verificação Local

Use Java 21 e Maven `3.9` ou superior:

```powershell
mvn clean verify
```

As releases são criadas apenas pelo workflow oficial do GitHub Actions. Consulte [RELEASING.md](RELEASING.md) para segredos, criação de tags e verificação no Maven Central.

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
