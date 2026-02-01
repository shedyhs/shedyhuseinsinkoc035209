package com.shedyhuseinsinkoc035209.controller;

import com.shedyhuseinsinkoc035209.dto.LoginRequest;
import com.shedyhuseinsinkoc035209.dto.LoginResponse;
import com.shedyhuseinsinkoc035209.dto.RefreshRequest;
import com.shedyhuseinsinkoc035209.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints de autenticação")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica o usuário e retorna tokens JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Gera um novo access token usando o refresh token")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }
}
