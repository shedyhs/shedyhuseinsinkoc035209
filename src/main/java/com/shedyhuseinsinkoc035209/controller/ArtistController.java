package com.shedyhuseinsinkoc035209.controller;

import com.shedyhuseinsinkoc035209.dto.ArtistRequest;
import com.shedyhuseinsinkoc035209.dto.ArtistResponse;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import com.shedyhuseinsinkoc035209.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
@Tag(name = "Artistas", description = "Endpoints para gerenciamento de artistas")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping
    @Operation(summary = "Criar artista", description = "Cria um novo artista")
    public ResponseEntity<ArtistResponse> create(@Valid @RequestBody ArtistRequest request) {
        ArtistResponse response = artistService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar artista por ID", description = "Retorna um artista pelo seu ID")
    public ResponseEntity<ArtistResponse> findById(@PathVariable UUID id) {
        ArtistResponse response = artistService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar artistas", description = "Lista todos os artistas com paginação, ordenados por nome ASC")
    public ResponseEntity<Page<ArtistResponse>> findAll(Pageable pageable) {
        Page<ArtistResponse> response = artistService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar artistas por nome", description = "Busca artistas pelo nome com ordenação")
    public ResponseEntity<Page<ArtistResponse>> findByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "asc") String order,
            Pageable pageable) {
        Page<ArtistResponse> response = artistService.findByName(name, order, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Buscar artistas por tipo", description = "Busca artistas pelo tipo (SOLO ou BAND)")
    public ResponseEntity<Page<ArtistResponse>> findByType(@PathVariable ArtistType type, Pageable pageable) {
        Page<ArtistResponse> response = artistService.findByType(type, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar artista", description = "Atualiza os dados de um artista")
    public ResponseEntity<ArtistResponse> update(@PathVariable UUID id, @Valid @RequestBody ArtistRequest request) {
        ArtistResponse response = artistService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir artista", description = "Remove um artista pelo ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
