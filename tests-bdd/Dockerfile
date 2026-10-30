# Imagem para rodar os testes BDD dentro do Docker (sem precisar de Java/Maven na maquina).
# Uso (a partir da raiz do repositorio):
#   docker compose --profile tests run --rm tests
FROM maven:3.9.8-eclipse-temurin-21

WORKDIR /tests

# Primeiro so o pom.xml: as dependencias ficam em cache e nao sao baixadas a cada mudanca nos testes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src

# A URL da API vem do docker-compose (BASE_URL=http://api:8080)
ENV BASE_URL=http://localhost:8080

# Suite completa (@regressivo). Sem "clean": a pasta target e um volume montado e nao pode ser apagada. Para so o smoke:  ... run --rm tests mvn -B test -Dtest=SmokeRunner
CMD ["mvn", "-B", "test"]
