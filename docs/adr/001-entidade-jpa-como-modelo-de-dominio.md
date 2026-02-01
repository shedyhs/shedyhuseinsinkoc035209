# ADR-001: Entidade JPA como Modelo de Dominio

**Status:** Aceita
**Data:** 2026-02-01

## Contexto

Em arquiteturas como Clean Architecture e Hexagonal, e comum separar a representacao de dominio (`Model`) da representacao de persistencia (`Entity`). Isso levanta a questao: devemos manter classes separadas para o modelo de dominio e para a entidade JPA, ou unifica-las em uma unica classe?

## Decisao

Optamos por manter a entidade JPA (`@Entity`) como a propria representacao do modelo de dominio, sem criar uma camada separada de `Model`.

## Justificativa

### Complexidade do dominio nao justifica a separacao

O dominio da aplicacao (Artistas, Albums, Imagens, Regionais) possui regras de negocio simples e operacoes predominantemente CRUD. Nao ha fluxos complexos de dominio, agregados com invariantes ricas ou eventos de dominio que justifiquem o isolamento em uma camada dedicada.

### A separacao critica ja existe: Entity e DTO

A API nunca expoe entidades JPA diretamente. Toda comunicacao com o mundo externo passa por DTOs implementados como Java Records (`ArtistRequest`, `ArtistResponse`, `AlbumRequest`, etc.). Essa e a fronteira que de fato importa para evitar acoplamento entre a API publica e o modelo interno.

### Menos boilerplate e menos mapeamentos

Separar `Entity` e `Model` exigiria:
- Classes duplicadas com campos similares
- Mappers bidirecionais (Entity <-> Model) em cada operacao
- Maior superficie de codigo para manter e testar

Para um dominio simples, isso adiciona complexidade acidental sem beneficio proporcional.

### Padrao consolidado no ecossistema Spring Boot

A abordagem de usar entidades JPA como modelo de dominio e o padrao mais adotado em projetos Spring Boot. Frameworks como Spring Data JPA sao projetados para trabalhar diretamente com entidades, e a comunidade reconhece essa pratica como adequada para aplicacoes de complexidade baixa a media.

### Logica de dominio nas proprias entidades

As entidades do projeto encapsulam comportamento relevante de forma coesa:
- `Album.addArtist()`, `Album.removeArtist()`, `Album.clearArtists()` gerenciam o relacionamento bidirecional
- `Region.deactivate()`, `Region.hasNameChanged()` encapsulam regras de sincronizacao
- `Artist.update()`, `Album.update()` centralizam a logica de atualizacao

Isso demonstra que as entidades nao sao meros "data holders" — elas possuem comportamento de dominio, o que e coerente com a unificacao.

## Alternativas consideradas

### Separar Entity e Domain Model

- **Vantagem:** Isola o dominio de anotacoes JPA e permite trocar o mecanismo de persistencia sem impacto no dominio.
- **Desvantagem:** Para este projeto, a troca de mecanismo de persistencia e um cenario improvavel, e o custo de mapeamento adicional nao se justifica pela complexidade atual.

## Consequencias

- Entidades JPA sao a unica representacao dos dados de dominio
- DTOs (Java Records) sao usados como fronteira entre a API e o dominio
- Caso o dominio evolua para regras mais complexas no futuro, essa decisao pode ser reavaliada com a introducao de uma camada de dominio separada
