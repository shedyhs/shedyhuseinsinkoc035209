package com.shedyhuseinsinkoc035209.controller;

import com.shedyhuseinsinkoc035209.dto.AlbumRequest;
import com.shedyhuseinsinkoc035209.dto.AlbumResponse;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import com.shedyhuseinsinkoc035209.service.AlbumService;
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
@RequestMapping("/api/v1/albums")
@Tag(name = "Álbuns", description = "Endpoints para gerenciamento de álbuns")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping
    @Operation(summary = "Criar álbum", description = "Cria um novo álbum associado a artistas")
    public ResponseEntity<AlbumResponse> create(@Valid @RequestBody AlbumRequest request) {
        AlbumResponse response = albumService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar álbum por ID", description = "Retorna um álbum pelo seu ID")
    public ResponseEntity<AlbumResponse> findById(@PathVariable UUID id) {
        AlbumResponse response = albumService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar álbuns", description = "Lista todos os álbuns com paginação")
    public ResponseEntity<Page<AlbumResponse>> findAll(Pageable pageable) {
        Page<AlbumResponse> response = albumService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Buscar álbuns por tipo de artista", description = "Busca álbuns pelo tipo do artista (SOLO ou BAND)")
    public ResponseEntity<Page<AlbumResponse>> findByArtistType(@PathVariable ArtistType type, Pageable pageable) {
        Page<AlbumResponse> response = albumService.findByArtistType(type, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/artist")
    @Operation(summary = "Buscar álbuns por nome do artista", description = "Busca álbuns pelo nome do artista")
    public ResponseEntity<Page<AlbumResponse>> findByArtistName(
            @RequestParam String name,
            @RequestParam(defaultValue = "asc") String order,
            Pageable pageable) {
        Page<AlbumResponse> response = albumService.findByArtistName(name, order, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar álbum", description = "Atualiza os dados de um álbum")
    public ResponseEntity<AlbumResponse> update(@PathVariable UUID id, @Valid @RequestBody AlbumRequest request) {
        AlbumResponse response = albumService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir álbum", description = "Remove um álbum pelo ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
