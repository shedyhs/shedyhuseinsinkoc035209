# API de Artistas e Álbuns

**Candidato:** Shedy Husein Sinkoc
**Inscrição:** 035209
**Vaga:** Analista de Tecnologia da Informação - Engenheiro da Computação (Sênior)
**Projeto:** Back-End

---

## Tecnologias Utilizadas

- **Java 17** com **Spring Boot 4.1.0-M1**
- **Spring Security** com JWT (jjwt 0.12.5)
- **Spring Data JPA** com PostgreSQL
- **Flyway** para migrações versionadas do banco de dados
- **MinIO** para armazenamento de imagens (API compatível com S3)
- **WebSocket (STOMP + SockJS)** para notificações em tempo real
- **Springdoc OpenAPI 2.8.4** (Swagger UI)
- **Bucket4j 8.7.0** para rate limiting
- **Spring Boot Actuator** para health checks e monitoramento
- **Docker & Docker Compose** para orquestração de containers
- **JUnit 5 + Mockito + AssertJ** para testes unitários
- **JaCoCo** para cobertura de testes
- **Checkstyle** para padronização de código

## Estrutura do Projeto

```
src/main/java/com/shedyhuseinsinkoc035209/
├── controller/          # Controllers REST (5)
│   ├── ArtistController
│   ├── AlbumController
│   ├── AlbumImageController
│   ├── AuthController
│   └── RegionController
├── service/             # Regras de negócio (7)
│   ├── ArtistService
│   ├── AlbumService
│   ├── AlbumImageService
│   ├── AuthService
│   ├── MinioService
│   ├── RegionService
│   └── CustomUserDetailsService
├── repository/          # Acesso a dados com Spring Data JPA (5)
├── entity/              # Entidades JPA (5) + Enums (2)
│   ├── Artist           # Artista (SOLO ou BAND)
│   ├── Album            # Álbum com relacionamento N:N com Artist
│   ├── AlbumImage       # Imagem de capa do álbum
│   ├── User             # Usuário para autenticação
│   └── Region           # Regional sincronizada com API externa
├── dto/                 # DTOs como Java Records (imutáveis)
├── config/              # Configurações (Security, MinIO, WebSocket, OpenAPI)
├── filter/              # Filtros HTTP (JWT, Rate Limiting)
├── client/              # Client para API externa de regionais
├── exception/           # Exceções customizadas e handler global
└── util/                # Utilitário JWT
```

## Modelo de Dados

```
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│     artists      │       │   artist_album   │       │     albums       │
├──────────────────┤       ├──────────────────┤       ├──────────────────┤
│ id       UUID PK │──┐    │ artist_id UUID FK│    ┌──│ id       UUID PK │
│ name     VARCHAR │  └───>│ album_id  UUID FK│<───┘  │ title    VARCHAR │
│ type     VARCHAR │       └──────────────────┘       │ release_year INT │
│ created_at TIMESTAMP│                               │ created_at TIMESTAMP│
│ updated_at TIMESTAMP│                               │ updated_at TIMESTAMP│
└──────────────────┘                                  └────────┬─────────┘
                                                               │ 1:N
┌──────────────────┐                                  ┌────────┴─────────┐
│     users        │                                  │  album_images    │
├──────────────────┤                                  ├──────────────────┤
│ id       UUID PK │                                  │ id       UUID PK │
│ username VARCHAR  │                                  │ album_id UUID FK │
│ password VARCHAR  │                                  │ file_name VARCHAR│
│ role     VARCHAR  │                                  │ object_key VARCHAR│
│ created_at TIMESTAMP│                               │ content_type VARCHAR│
│ updated_at TIMESTAMP│                               │ created_at TIMESTAMP│
└──────────────────┘                                  └──────────────────┘

┌──────────────────┐
│    regions       │
├──────────────────┤
│ id       BIGINT PK (surrogate) │
│ external_id INT  │
│ name     VARCHAR(200) │
│ active   BOOLEAN │
└──────────────────┘
```

**Relacionamentos:**
- **Artist ↔ Album**: Many-to-Many (tabela intermediária `artist_album`)
- **Album → AlbumImage**: One-to-Many com cascade e orphan removal

**Decisões sobre o modelo:**
- **UUID como PK** para Artist, Album, AlbumImage e User, evitando colisões e exposição de sequências
- **Surrogate key (BIGINT)** na tabela `regions` para suportar múltiplas versões de uma mesma regional (quando o nome muda, inativa o antigo e cria novo registro)
- **`external_id`** na Region permite rastrear a mesma regional da API externa mesmo após inativação/recriação

## Como Executar

### Com Docker Compose (Recomendado)

```bash
docker-compose up -d
```

Isso irá iniciar:
- **PostgreSQL** na porta 5433 (mapeado de 5432 interno)
- **MinIO** nas portas 9000 (API) e 9001 (Console)
- **API** na porta 8081 (mapeado de 8080 interno)

### Desenvolvimento Local

Pré-requisitos: PostgreSQL e MinIO rodando localmente.

```bash
./mvnw spring-boot:run
```

Neste modo, a API roda na porta 8080.

### Dados Iniciais

O banco já vem populado via Flyway com os dados de exemplo do edital:

| Artista | Tipo | Álbuns |
|---------|------|--------|
| Serj Tankian | SOLO | Harakiri, Black Blooms, The Rough Dog |
| Mike Shinoda | SOLO | The Rising Tied, Post Traumatic, Post Traumatic EP, Where'd You Go |
| Michel Teló | SOLO | Bem Sertanejo, Bem Sertanejo - O Show (Ao Vivo), Bem Sertanejo - (1ª Temporada) - EP |
| Guns N' Roses | BAND | Use Your Illusion I, Use Your Illusion II, Greatest Hits |

**Usuário de teste:** `admin` / `admin123`

## URLs

| Serviço | URL (Docker) | URL (Local) |
|---------|-------------|-------------|
| API | http://localhost:8081 | http://localhost:8080 |
| Swagger UI | http://localhost:8081/swagger-ui/index.html | http://localhost:8080/swagger-ui/index.html |
| MinIO Console | http://localhost:9001 | http://localhost:9001 |
| Health Check | http://localhost:8081/actuator/health | http://localhost:8080/actuator/health |
| Liveness Probe | http://localhost:8081/actuator/health/liveness | http://localhost:8080/actuator/health/liveness |
| Readiness Probe | http://localhost:8081/actuator/health/readiness | http://localhost:8080/actuator/health/readiness |

## Autenticação

A API utiliza JWT (JSON Web Token) com expiração de **5 minutos** e refresh token com expiração de **24 horas**.

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Usando o Token

```bash
curl -X GET http://localhost:8080/api/v1/artists \
  -H "Authorization: Bearer <seu_token_aqui>"
```

### Renovar Token

```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<seu_refresh_token>"}'
```

## Endpoints

### Autenticação (`/api/v1/auth`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/login` | Realizar login, retorna access token e refresh token |
| POST | `/refresh` | Renovar access token usando refresh token |

### Artistas (`/api/v1/artists`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/` | Criar artista |
| GET | `/{id}` | Buscar por ID |
| GET | `/` | Listar todos (paginado, ordenado por nome ASC) |
| GET | `/search?name=&order=` | Buscar por nome com ordenação (asc/desc) |
| GET | `/type/{type}` | Filtrar por tipo (SOLO ou BAND) |
| PUT | `/{id}` | Atualizar artista |
| DELETE | `/{id}` | Excluir artista |

### Álbuns (`/api/v1/albums`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/` | Criar álbum (com lista de artistIds) |
| GET | `/{id}` | Buscar por ID |
| GET | `/` | Listar todos (paginado) |
| GET | `/type/{type}` | Filtrar por tipo de artista |
| GET | `/artist?name=&order=` | Buscar por nome do artista com ordenação |
| PUT | `/{id}` | Atualizar álbum |
| DELETE | `/{id}` | Excluir álbum |

### Imagens de Álbuns (`/api/v1/albums`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/{albumId}/images` | Upload de imagens (multipart/form-data) |
| GET | `/{albumId}/images` | Listar imagens com links pré-assinados (30 min) |
| DELETE | `/images/{imageId}` | Excluir imagem do MinIO e do banco |

### Regionais (`/api/v1/regions`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/` | Listar todas as regionais |
| GET | `/active` | Listar regionais ativas |
| POST | `/sync` | Sincronizar com API externa |

### WebSocket
| Endpoint | Descrição |
|----------|-----------|
| `/ws` | Conexão STOMP + SockJS |
| `/topic/albums` | Tópico que recebe notificações a cada novo álbum cadastrado |

### Actuator
| Endpoint | Descrição |
|----------|-----------|
| `/actuator/health` | Status geral da aplicação e componentes |
| `/actuator/health/liveness` | Liveness probe (Kubernetes) |
| `/actuator/health/readiness` | Readiness probe (Kubernetes) |
| `/actuator/info` | Informações da aplicação |
| `/actuator/metrics` | Métricas da aplicação |

## Testes

### Executar testes

```bash
./mvnw test
```

### Executar com relatório de cobertura (JaCoCo)

```bash
./mvnw clean test
open target/site/jacoco/index.html
```

### Cobertura atual

| Camada | Statements | Branches |
|--------|-----------|----------|
| Services | 100% | 100% |
| Entities | 100% | 100% |
| Filters | 100% | 100% |
| Controllers | 100% | 100% |
| DTOs | 100% | 100% |
| Exceptions | 100% | 100% |
| Util | ~97% | 100% |
| **Total** | **~97%** | **~97%** |

### Arquivos de teste

**Services (7):** ArtistServiceTest, AlbumServiceTest, AlbumImageServiceTest, AuthServiceTest, MinioServiceTest, RegionServiceTest, CustomUserDetailsServiceTest

**Controllers (5):** ArtistControllerTest, AlbumControllerTest, AlbumImageControllerTest, AuthControllerTest, RegionControllerTest

**Entities (5):** ArtistTest, AlbumTest, AlbumImageTest, UserTest, RegionTest

**Filters (2):** JwtAuthenticationFilterTest, RateLimitFilterTest

**Config (4):** SecurityConfigTest, MinioConfigTest, WebSocketConfigTest, OpenApiConfigTest

**Outros:** JwtUtilTest, GlobalExceptionHandlerTest, RegionExternalClientImplTest

## Checklist de Requisitos

### Requisitos Gerais
- [x] Segurança CORS - bloqueia acesso de domínios fora do serviço
- [x] Autenticação JWT com expiração a cada 5 minutos e renovação
- [x] Implementação dos verbos POST, PUT, GET (e DELETE)
- [x] Paginação na consulta dos álbuns
- [x] Consultas parametrizadas (álbuns por cantores e/ou bandas)
- [x] Consultas por nome do artista com ordenação alfabética (asc/desc)
- [x] Upload de uma ou mais imagens de capa do álbum
- [x] Armazenamento das imagens no MinIO (API S3)
- [x] Recuperação por links pré-assinados com expiração de 30 minutos
- [x] Versionamento de endpoints (`/api/v1/...`)
- [x] Flyway Migrations para criar e popular tabelas
- [x] Documentação de endpoints com OpenAPI/Swagger

### Requisitos Sênior
- [x] Health Checks com Liveness e Readiness probes
- [x] Testes unitários com cobertura de ~97% (entity, service e filter com 100%)
- [x] WebSocket para notificar o front a cada novo álbum cadastrado
- [x] Rate limit: até 10 requisições por minuto por usuário
- [x] Endpoint de regionais com sincronização inteligente

### Instruções
- [x] Projeto em repositório GitHub
- [x] README.md com documentação, dados de inscrição, vaga e como executar/testar
- [x] Relacionamento Artista-Álbum N:N
- [x] Exemplos do edital como carga inicial do banco
- [x] Aplicação empacotada como imagem Docker
- [x] Containers orquestrados (API + MinIO + BD) via docker-compose

## Decisões Técnicas

| Decisão | Justificativa |
|---------|---------------|
| **Sem Lombok** | Getters, setters e construtores manuais conforme boas práticas do projeto |
| **Java Records para DTOs** | DTOs imutáveis, concisos e sem boilerplate |
| **UUID como chave primária** | Evita exposição de sequências e colisões em ambientes distribuídos |
| **Surrogate key em Region** | Suporta o cenário de inativar registro antigo e criar novo quando o nome muda |
| **BCrypt para senhas** | Hash seguro com salt automático |
| **Flyway** | Migrações versionadas e reproduzíveis para controle do schema |
| **Bucket4j para rate limiting** | In-memory, leve, com cleanup automático de buckets expirados |
| **Multi-stage Docker build** | Imagem final usa JRE para menor tamanho (~200MB vs ~700MB com JDK) |
| **STOMP + SockJS** | Protocolo padrão para WebSocket com fallback para browsers que não suportam |
| **RestClient** | HTTP client moderno do Spring 6, substituto do RestTemplate |
| **Checkstyle** | Garante padronização de código em todo o projeto |
| **JaCoCo** | Relatório de cobertura de testes integrado ao build |

## Sincronização de Regionais

A sincronização com a API externa segue a lógica:

1. **Novo no endpoint** → insere na tabela local como ativo
2. **Ausente no endpoint** → inativa na tabela local
3. **Nome alterado** → inativa o registro antigo e cria novo registro ativo

A tabela `regions` usa um **surrogate key** (id autoincremental) separado do `external_id`, permitindo manter o histórico de alterações sem perder referências.
