# API de Artistas e Álbuns

**Candidato:** Shedy Husein Sinkoc
**Inscrição:** 035209

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 4.1.0-M1**
- **Spring Security** com JWT (jjwt 0.12.5)
- **Spring Data JPA** com PostgreSQL
- **Flyway** para migrações de banco de dados
- **MinIO** para armazenamento de imagens
- **WebSocket (STOMP + SockJS)** para notificações em tempo real
- **Springdoc OpenAPI 2.8.4** (Swagger UI)
- **Bucket4j 8.7.0** para rate limiting
- **Docker & Docker Compose**
- **JUnit 5 + Mockito + AssertJ** para testes

## Como Executar

### Com Docker Compose (Recomendado)

```bash
docker-compose up -d
```

Isso irá iniciar:
- **PostgreSQL** na porta 5432
- **MinIO** nas portas 9000 (API) e 9001 (Console)
- **API** na porta 8080

### Desenvolvimento Local

Pré-requisitos: PostgreSQL e MinIO rodando localmente.

```bash
./mvnw spring-boot:run
```

## URLs

| Serviço | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| MinIO Console | http://localhost:9001 |
| Actuator | http://localhost:8080/actuator |

## Autenticação

A API utiliza JWT (JSON Web Token) para autenticação.

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
| POST | `/login` | Realizar login |
| POST | `/refresh` | Renovar token |

### Artistas (`/api/v1/artists`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/` | Criar artista |
| GET | `/{id}` | Buscar por ID |
| GET | `/` | Listar todos (paginado) |
| GET | `/search?name=&order=` | Buscar por nome |
| GET | `/type/{type}` | Buscar por tipo |
| PUT | `/{id}` | Atualizar artista |
| DELETE | `/{id}` | Excluir artista |

### Álbuns (`/api/v1/albums`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/` | Criar álbum |
| GET | `/{id}` | Buscar por ID |
| GET | `/` | Listar todos (paginado) |
| GET | `/type/{type}` | Buscar por tipo de artista |
| GET | `/artist?name=&order=` | Buscar por nome do artista |
| PUT | `/{id}` | Atualizar álbum |
| DELETE | `/{id}` | Excluir álbum |

### Imagens de Álbuns (`/api/v1/albums`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/{albumId}/images` | Upload de imagens |
| GET | `/{albumId}/images` | Listar imagens |
| DELETE | `/images/{imageId}` | Excluir imagem |

### Regionais (`/api/v1/regions`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/` | Listar todas |
| GET | `/active` | Listar ativas |
| POST | `/sync` | Sincronizar com API externa |

## Testes

```bash
./mvnw test
```

Testes incluem:
- **ArtistServiceTest** - CRUD completo de artistas
- **AlbumServiceTest** - CRUD de álbuns com verificação de WebSocket
- **AuthServiceTest** - Login, refresh e cenários de erro
- **JwtUtilTest** - Geração e validação de tokens JWT
- **ArtistControllerTest** - Testes de integração com MockMvc

## Checklist

- [x] CRUD de Artistas
- [x] CRUD de Álbuns com associação Many-to-Many
- [x] Upload de imagens com MinIO
- [x] Autenticação JWT (login + refresh)
- [x] Rate Limiting (10 req/min)
- [x] WebSocket para notificações de novos álbuns
- [x] Sincronização de regionais com API externa
- [x] Swagger/OpenAPI documentação
- [x] Flyway migrações
- [x] Docker e Docker Compose
- [x] Testes unitários
- [x] Actuator para monitoramento

## Decisões Técnicas

- **Sem Lombok**: Todos os getters, setters e construtores são manuais conforme requisito
- **UUID como chave primária**: Utiliza `GenerationType.UUID` do JPA
- **Jakarta EE**: Spring Boot 4.x utiliza `jakarta.*` em vez de `javax.*`
- **BCrypt**: Senhas armazenadas com hash BCrypt
- **Flyway**: Migrações versionadas para controle do schema do banco
- **Rate Limiting com Bucket4j**: 10 requisições por minuto por usuário/IP
- **Multi-stage Docker build**: Imagem final utiliza JRE Alpine para menor tamanho
- **STOMP over WebSocket**: Para notificações em tempo real de novos álbuns
- **RestClient**: Utilizado para comunicação com API externa de regionais
