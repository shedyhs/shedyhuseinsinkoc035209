# ADR-002: Bucket4j para Rate Limiting

**Status:** Aceita
**Data:** 2026-02-01

## Contexto

A aplicacao precisa limitar o numero de requisicoes por usuario/IP para proteger a API contra abuso e garantir disponibilidade. O edital exige rate limiting de ate 10 requisicoes por minuto por usuario. Era necessario escolher uma estrategia e uma biblioteca para implementar esse controle.

## Decisao

Optamos por utilizar o **Bucket4j** com armazenamento in-memory (`ConcurrentHashMap`) implementado como um filtro HTTP do Spring (`OncePerRequestFilter`).

## Justificativa

### Algoritmo Token Bucket

O Bucket4j implementa o algoritmo Token Bucket, que e amplamente utilizado em sistemas de rate limiting por oferecer:
- **Previsibilidade:** cada usuario recebe um numero fixo de tokens que sao reabastecidos em intervalos regulares
- **Tolerancia a rajadas:** permite rajadas curtas de requisicoes desde que haja tokens disponiveis, sem penalizar o usuario por picos momentaneos
- **Simplicidade conceitual:** facil de entender, configurar e depurar

### Sem dependencia de infraestrutura externa

Alternativas como Redis ou bancos de dados requerem infraestrutura adicional para armazenar contadores. Para esta aplicacao, que roda como instancia unica, o armazenamento in-memory e suficiente e elimina:
- Latencia de rede para cada verificacao de rate limit
- Ponto adicional de falha (se o Redis cair, a API tambem seria afetada)
- Complexidade de configuracao e operacao

### Biblioteca leve e sem dependencias transitivas pesadas

O Bucket4j e uma biblioteca focada exclusivamente em rate limiting, com footprint minimo. Nao traz consigo frameworks ou dependencias que inflam o classpath, ao contrario de solucoes mais completas como o Spring Cloud Gateway que incluem muito mais do que o necessario.

### Configuracao externalizada

Os parametros de rate limiting (`capacity`, `refill-period-minutes`, `expiration-minutes`) sao configurados via `application.yml`, permitindo ajuste sem recompilacao:

```yaml
rate-limit:
  capacity: 10
  refill-period-minutes: 1
  expiration-minutes: 2
```

### Cleanup automatico de buckets expirados

A implementacao inclui um `ScheduledExecutorService` que periodicamente remove buckets inativos da memoria, evitando memory leak em cenarios com muitos IPs/usuarios distintos.

### Identificacao inteligente do cliente

O filtro identifica o cliente por username (quando autenticado) ou por IP (quando anonimo), garantindo que o rate limit se aplique de forma justa independente do estado de autenticacao.

## Alternativas consideradas

### Spring Cloud Gateway Rate Limiter

- **Vantagem:** Integrado ao ecossistema Spring Cloud, suporta Redis nativamente para ambientes distribuidos.
- **Desvantagem:** Requer Spring Cloud Gateway como dependencia, o que e desproporcional para uma API standalone. Adiciona complexidade arquitetural significativa para resolver um problema simples.

### Resilience4j RateLimiter

- **Vantagem:** Parte do ecossistema Resilience4j, com integracao Spring Boot e metricas.
- **Desvantagem:** O modulo de rate limiting do Resilience4j e orientado a chamadas de servico (client-side), nao a protecao de endpoints (server-side). Nao oferece controle por usuario/IP nativamente.

### Guava RateLimiter

- **Vantagem:** Biblioteca consolidada do Google, sem dependencias externas.
- **Desvantagem:** O `RateLimiter` do Guava e blocking (bloqueia a thread ate o token estar disponivel) em vez de retornar rejeicao imediata. Nao e adequado para APIs HTTP onde a resposta deve ser 429 imediata.

### Redis + contador manual

- **Vantagem:** Funciona em ambientes distribuidos com multiplas instancias.
- **Desvantagem:** Adiciona dependencia de infraestrutura que nao se justifica para uma aplicacao single-instance. O Redis ja esta fora do escopo dos containers definidos no docker-compose.

## Consequencias

- Rate limiting funciona in-memory, adequado para deploy single-instance
- Buckets expirados sao removidos automaticamente a cada 2 minutos
- Configuracao ajustavel via `application.yml` sem recompilacao
- Caso a aplicacao evolua para multiplas instancias, sera necessario migrar para uma solucao distribuida (Bucket4j suporta integracao com Redis/Hazelcast, facilitando a transicao)
