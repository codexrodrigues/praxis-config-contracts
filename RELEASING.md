# Guia de Release — Praxis Config Contracts

## Linha pública

A linha pública vigente é `0.1.0-beta.*`. Correções compatíveis permanecem nessa linha enquanto o contrato é validado por `praxis-config-starter`, Quickstart e aplicações corporativas consumidoras. Um release estável exige consumo downstream verificado; uma nova versão major requer plano governado de breaking change.

## Segredos obrigatórios no repositório

- `CENTRAL_TOKEN_USER`
- `CENTRAL_TOKEN_PASS`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`
- `GPG_KEY_ID` (opcional)
- `RELEASE_PAT`, com permissão para enviar alterações para o repositório

`RELEASE_PAT` é necessário porque a tag criada pelo workflow precisa disparar a execução separada de publicação pela tag.

## Fluxo de release

1. Confirme que o commit pretendido está em `main` remoto e que a validação local está verde.
2. Execute localmente com Java 21:

   ```powershell
   mvn -B clean verify
   ```

3. No GitHub Actions, execute **Release Praxis Config Contracts** em `main` com `create_tag=true` e uma versão explícita ou `bump`/`preid`.
4. O workflow grava a versão resolvida no `pom.xml`, cria a tag anotada `v<semver>` e envia ambos atomicamente.
5. A execução pela tag verifica ancestralidade, versão marcada e dependências leves, roda os testes, assina POM/JAR/sources/Javadocs e publica pelo Portal do Maven Central.
6. Aguarde a aprovação da sondagem de disponibilidade no Maven Central.
7. Compile um consumidor isolado contra a coordenada pública. Instalação local no Maven não constitui evidência de release.

Não crie tags de release manualmente, não execute `mvn deploy` localmente nem contorne um gate de CI/release que falhou.
