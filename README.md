# SmartQueue 🚀

Sistema distribuído de processamento assíncrono inspirado em filas corporativas de alta performance. Implementado com Arquitetura Hexagonal, focado em resiliência, retry automático e observabilidade completa.

## Tecnologias 🛠️
* **Java 21** & **Spring Boot 3.3**
* **Apache Kafka** (Mensageria, DLQ e Retentativas)
* **Redis** (Persistência rápida de estado e controle)
* **Prometheus** (Métricas e Observabilidade)
* **Docker & Docker Compose** (Containerização e orquestração local)
* **OpenAPI 3.0 (Swagger)** (Documentação da API)

## Arquitetura 🏗️
O projeto segue a **Arquitetura Hexagonal** (Ports and Adapters):
1. **Domain**: Modelos centrais e regras de negócio (`Task`, `TaskStatus`).
2. **Application**: Casos de uso (`ProcessTaskUseCase`) e Portas (interfaces) de entrada/saída.
3. **Infrastructure**: Adaptadores que interagem com o mundo externo (Kafka, Redis, Web Controllers).

## Como Executar 🏃‍♂️

A aplicação está dockerizada usando *multi-stage build*, o que significa que você não precisa instalar o Java ou Gradle na sua máquina, apenas o **Docker** e o **Docker Compose**.

1. **Subir toda a infraestrutura e a aplicação:**
```bash
docker-compose up --build -d
```

Isso iniciará:
* Zookeeper & Kafka
* Redis
* Prometheus
* Aplicação Spring Boot (SmartQueue)

2. **Acessar o Swagger UI:**
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

3. **Métricas Prometheus:**
[http://localhost:9090](http://localhost:9090) (Acesse a UI do Prometheus para consultar métricas da aplicação geradas pelo Micrometer).

## Fluxo de Processamento 🔄
1. Uma requisição `POST /api/v1/tasks` contendo o `payload` é recebida.
2. O estado da tarefa é salvo no **Redis** como `PENDING`.
3. O evento é publicado no tópico principal do **Kafka** (`tasks.main`).
4. O `KafkaTaskConsumer` consome a mensagem e executa a lógica de negócio.
5. Se ocorrer um erro durante o processamento:
   * A tarefa é salva novamente no **Redis** e enviada para o tópico de repetição (`tasks.retry`).
   * A repetição ocorre até atingir `max-retries` (padrão 3 vezes).
   * Se falhar todas as vezes, a mensagem é direcionada para a fila morta ou Dead Letter Queue (`tasks.dlq`) e o status muda para `DLQ`.
6. Você pode verificar o status atual da tarefa via `GET /api/v1/tasks/{id}`.
