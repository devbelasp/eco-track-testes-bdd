# EcoTrack – Testes automatizados BDD (Fase 7)

Testes automatizados **BDD** (Cucumber + Gherkin) e de **API** (Rest Assured + JSON Schema) da API **EcoTrack**,
um serviço de agendamento e rastreabilidade de coletas de resíduos (tema **ESG**: gestão de resíduos e reciclagem).
A API foi construída na Fase 6 (Java 21 + Spring Boot 4 + PostgreSQL) e agora ganhou uma suíte de testes que valida
o comportamento dela pelo ponto de vista de quem a consome, **contra a API e um PostgreSQL reais, sem mocks**.

**Integrantes (FIAP):** Thiago Andrade Silvano · Isabela dos Santos Pinto · Laura Elvira Naun · Isabelly Romano Tempesta

---

## O que é testado

20 cenários em Gherkin (24 execuções, porque um cenário roda uma vez para cada categoria de resíduo),
com **caminhos felizes e de falha**, cobrindo os 5 endpoints de coletas e o health check.

| Funcionalidade (`.feature`) | Cenário | Resultado esperado |
|---|---|---|
| **Agendamento** (`POST`) | Agendar uma coleta válida `@smoke` | 201, dados devolvidos, status `AGENDADO` |
| | Agendar uma coleta para cada categoria aceita (Esquema do Cenário, 5 exemplos) | 201 em todas |
| | Peso igual a zero | 400 + mensagem do campo `pesoKg` |
| | Data no passado | 400 + mensagem do campo `dataAgendamento` |
| | Categoria inexistente | 400 + "Corpo da requisição inválido..." |
| | Sem o nome da empresa | 400 + mensagem do campo `empresaGeradora` |
| **Consulta** (`GET`) | Buscar coleta existente por ID `@smoke` | 200 + dados corretos |
| | Buscar ID inexistente | 404 + mensagem |
| | Buscar ID que não é número | 400 + mensagem |
| | Listar com paginação `@smoke` | 200, lista não vazia, no máximo `size` itens |
| **Atualização** (`PUT`) | Atualizar coleta existente | 200, novos dados, status continua `AGENDADO` |
| | Atualizar ID inexistente | 404 |
| | Atualizar com dados inválidos | 400 |
| **Cancelamento** (`DELETE`) | Cancelar coleta existente | 204 e a coleta deixa de existir (GET → 404) |
| | Cancelar ID inexistente | 404 |
| **Saúde** | Health check `@smoke` | 200 e `status: UP` |
| **Contrato** (JSON Schema) | Coleta agendada segue o schema `coleta` | sem violações |
| | Listagem paginada segue o schema `pagina-coletas` | sem violações |
| | Erro 404 segue o schema `erro` | sem violações |
| | Erro de validação segue o schema `erro-validacao` | sem violações |

Em cada cenário os testes conferem o **status code**, o **corpo JSON da resposta** e, nos cenários de contrato,
a **estrutura da resposta contra um JSON Schema** (`tests-bdd/src/test/resources/schemas`).

Exemplo de cenário (arquivo `agendamento.feature`):

```gherkin
# language: pt
@regressivo
Funcionalidade: Agendamento de coletas de resíduos

  @smoke
  Cenário: Agendar uma coleta válida
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | Empresa Verde Ltda |
      | categoriaResiduo | PLASTICO           |
      | pesoKg           | 12.5               |
      | dataAgendamento  | daqui a 7 dias     |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 201
    E os dados da coleta devolvidos devem ser iguais aos informados
    E o status de destinação da coleta deve ser "AGENDADO"
```

---

## Como executar

### Pré-requisitos

- **Docker Desktop** instalado e **aberto** (é tudo de que a opção A precisa).
- Opção B (opcional): **JDK 21** e **Maven 3.9+** na sua máquina. O Cucumber deste curso usa Java 21.

### Opção A – tudo no Docker (a mais simples)

Na raiz do repositório:

```bash
# 1. Crie o arquivo de variáveis a partir do exemplo (Linux/macOS)
cp .env.example .env
#    Windows (PowerShell/CMD):  copy .env.example .env

# 2. Suba o PostgreSQL e a API e espere os dois ficarem saudáveis
docker compose up --build -d --wait

# 3. Rode os testes BDD (em um container, contra a API acima)
docker compose --profile tests run --rm tests

# 4. (Opcional) Rode só os testes rápidos, marcados com @smoke
docker compose --profile tests run --rm tests mvn -B test -Dtest=SmokeRunner

# 5. Ao terminar, derrube tudo (o -v apaga o volume do banco)
docker compose down -v
```

Na primeira vez, o passo 3 demora alguns minutos porque baixa as dependências do Maven.
O relatório HTML fica em `tests-bdd/target/cucumber-reports/regressivo.html`.

**Resultado esperado:** `24 Scenarios (24 passed)`, `108 Steps (108 passed)` e `BUILD SUCCESS`
na suíte completa; `4 Scenarios (4 passed)` e `19 Steps (19 passed)` no smoke.

### Opção B – Maven na sua máquina

Com a API e o banco no ar (passos 1 e 2 acima):

```bash
cd tests-bdd

mvn clean test                     # suíte completa (tag @regressivo)
mvn test -Dtest=SmokeRunner        # só os testes rápidos (tag @smoke)
```

Se a API estiver em outro endereço, informe a URL: `BASE_URL=http://localhost:8081 mvn clean test`
(no PowerShell: `$env:BASE_URL="http://localhost:8081"; mvn clean test`).

### Opção C – IntelliJ IDEA

Instale o plugin **Cucumber for Java**, abra a pasta `tests-bdd` como projeto Maven, abra qualquer arquivo
`.feature` e clique no botão ▶ ao lado de um cenário. Ou execute a classe `runner.TestRunner`.

### Problemas comuns

| Sintoma | Solução |
|---|---|
| `Cannot connect to the Docker daemon` | Abra o Docker Desktop e espere ele iniciar |
| `port is already allocated` (8080 ou 5432) | Outro programa usa a porta. Pare-o ou mude `APP_PORT` / `DB_PORT` no `.env` (e use `BASE_URL` na opção B) |
| A API fica `unhealthy` | Veja o motivo com `docker compose logs api`. Se mudou a senha no `.env` depois de subir, rode `docker compose down -v` e suba de novo |
| `.env` não encontrado | Execute o passo 1 (copiar `.env.example`) na raiz do repositório |

### Relatórios gerados

Em `tests-bdd/target/cucumber-reports/`: `regressivo.html` (visual), `regressivo.json` e `regressivo.xml`.
O smoke gera `smoke.html`. No GitHub Actions, a pasta é publicada como artefato **relatorio-cucumber**.

---

## Banco de dados real

Os testes não usam mocks nem banco em memória: o `docker-compose.yml` sobe um **PostgreSQL 16** real, a API aplica as
migrations do **Flyway** (`V1` e `V2`) e cada requisição dos cenários grava e lê dados de verdade.

Para manter o banco limpo e os cenários **independentes entre si**, o hook `@After` (`hook/Hook.java`) apaga do
banco, depois de cada cenário, as coletas que ele criou. Nenhum cenário depende de ID fixo ou da ordem de execução:
quando precisam de uma coleta existente, eles a criam antes (`Contexto:`).

## Tags

| Tag | Uso | Como rodar |
|---|---|---|
| `@regressivo` | todos os cenários: confere se o que já funcionava continua funcionando | `mvn clean test` |
| `@smoke` | poucos cenários rápidos com o essencial (health, agendar, buscar, listar) | `mvn test -Dtest=SmokeRunner` |

Um cenário pode ter as duas tags: o `@regressivo` fica na funcionalidade e o `@smoke` só nos cenários principais.

## Estrutura do repositório

```
.
├── Dockerfile · docker-compose.yml · .env.example   API + PostgreSQL (+ serviço "tests")
├── pom.xml · src/                                   código e testes da API (Fase 6)
├── tests-bdd/                                       PROJETO DE TESTES BDD
│   ├── Dockerfile · pom.xml
│   └── src/test/
│       ├── java/
│       │   ├── steps/      tradução de cada passo do Gherkin para Java
│       │   ├── services/   chamadas HTTP (Rest Assured), contrato (JSON Schema), datas
│       │   ├── model/      modelo da coleta (Gson)
│       │   ├── context/    estado compartilhado de um cenário
│       │   ├── hook/       @BeforeAll (espera a API) e @After (limpa o banco)
│       │   └── runner/     TestRunner (@regressivo) e SmokeRunner (@smoke)
│       └── resources/
│           ├── features/   os cenários em Gherkin
│           └── schemas/    contratos em JSON Schema
└── .github/workflows/ci.yml                         pipeline (GitHub Actions)
```

## Pipeline (GitHub Actions)

O workflow `.github/workflows/ci.yml` roda a cada push, a cada Pull Request e também manualmente:

1. **Build e testes da API** – `mvn verify` com PostgreSQL 16 como service container (testes da Fase 6).
2. **Testes BDD (Cucumber)** – sobe API + PostgreSQL com `docker compose up --build -d --wait`, executa
   `mvn clean test` na pasta `tests-bdd`, publica o relatório do Cucumber e derruba os containers.
3. **Build da imagem Docker** – valida o Dockerfile (somente em Pull Request).

Se algum teste falhar, o job fica vermelho e o pipeline é interrompido. O deploy da Fase 6 (Azure) foi removido
deste repositório: esta atividade é de testes e roda localmente ou em CI.

## Decisões e limitações

- **Datas relativas:** a API só aceita data de hoje em diante. Os cenários escrevem "daqui a 7 dias" e o teste calcula
  a data real (em UTC, igual ao relógio do container), então a suíte não "vence" com o tempo.
- **Contrato da listagem:** o schema `pagina-coletas` exige apenas o campo `content` (lista de coletas), porque o
  restante do formato de paginação é do Spring Data e pode mudar entre versões.
- **Sem autenticação:** a API da Fase 6 não tem login, então os testes não precisam de token.
- **ID inexistente:** os cenários de 404 usam o ID `999999999`, que não existe no banco de teste.
- **Dados em branco na tabela:** o valor `[vazio]` representa um texto em branco nas tabelas do Gherkin.

## Tecnologias utilizadas

Java 21 · Spring Boot 4.0.6 · PostgreSQL 16 · Flyway · Docker e Docker Compose · GitHub Actions ·
Cucumber 7.18.1 (Gherkin, JUnit, PicoContainer) · Rest Assured 5.5.0 · Gson 2.11.0 ·
JSON Schema Validator (networknt) 1.5.1 · Maven 3.9

## Requisitos da atividade × onde estão atendidos

| Requisito | Atendimento |
|---|---|
| Mínimo de 3 cenários BDD em Gherkin, positivos e negativos | 20 cenários em `tests-bdd/src/test/resources/features` |
| Testes de API para todas as APIs | `POST`, `GET` (lista e por ID), `PUT`, `DELETE` de `/api/residuos/agendamentos` + `/actuator/health` |
| Validação de status code | passo `o status code da resposta deve ser {int}` em todos os cenários |
| Validação do corpo JSON | passos de dados devolvidos, mensagens de erro, `campos` e lista |
| Testes de contrato com JSON Schema | `contrato.feature` + 4 schemas em `src/test/resources/schemas` |
| Execução simples e documentada (local ou CI) | seção "Como executar" + `ci.yml` |
| Dockerfiles, scripts de configuração e README no ZIP | `tests-bdd/Dockerfile`, `Dockerfile`, `docker-compose.yml`, `.env.example`, este README |
| PDF/PPT com cenários em Gherkin e evidências | documentação técnica entregue junto com o ZIP |
