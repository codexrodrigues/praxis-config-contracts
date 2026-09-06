# Actions no fechamento de versões


O padrão durante desenvolvimento é zero execuções remotas. Valide localmente o escopo alterado; commits, PRs, documentação interna e conclusão de tarefas não são motivos para iniciar Actions. Use os workflows manuais somente no fechamento autorizado de uma versão/publicação ou na prova necessária do host já implantado. Não use `[skip ci]` como mecanismo principal nem desabilite checks/proteções para economizar.

Antes de push, tag ou dispatch, confira os gatilhos reais de `.github/workflows/`. Tags de release publicam artefatos: não criá-las para testar a automação. Diagnostique localmente antes de repetir um job; conserve a evidência da revisão e dos artefatos usados. Monitores operacionais explicitamente mantidos são independentes do CI de commits. Consulte [ACTIONS-RELEASE-POLICY.md](ACTIONS-RELEASE-POLICY.md) para os pontos de entrada e recuperação.

## Fluxo deste repositório

O CI isolado (`ci.yml`) fica disponível por dispatch somente para um gate de fechamento necessário. A release normal já executa a validação de dependências leves e `clean verify` antes de assinar/publicar na mesma sessão Maven; não rode o CI isolado por reflexo antes de cada release.

Use `release.yml` em main com create_tag=true: POM e tag são persistidos atomicamente. A tag v* precisa pertencer a main e corresponder à versão do POM. Propagação no Central e prova downstream continuam obrigatórias. Consulte RELEASING.md.
