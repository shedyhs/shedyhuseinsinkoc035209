package com.shedyhuseinsinkoc035209.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadCredentials_shouldReturn401() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<Map<String, Object>> response = handler.handleBadCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("status", 401);
        assertThat(response.getBody()).containsEntry("error", "Unauthorized");
        assertThat(response.getBody()).containsEntry("message", "Bad credentials");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleUsernameNotFound_shouldReturn401() {
        UsernameNotFoundException ex = new UsernameNotFoundException("User not found: admin");

        ResponseEntity<Map<String, Object>> response = handler.handleUsernameNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("status", 401);
        assertThat(response.getBody()).containsEntry("error", "Unauthorized");
        assertThat(response.getBody()).containsEntry("message", "User not found: admin");
    }

    @Test
    void handleResourceNotFound_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Album not found");

        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("status", 404);
        assertThat(response.getBody()).containsEntry("error", "Not Found");
        assertThat(response.getBody()).containsEntry("message", "Album not found");
    }

    @Test
    void handleInfrastructure_shouldReturn500() {
        InfrastructureException ex = new InfrastructureException("Failed to upload", new RuntimeException("IO error"));

        ResponseEntity<Map<String, Object>> response = handler.handleInfrastructure(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("status", 500);
        assertThat(response.getBody()).containsEntry("error", "Internal Server Error");
        assertThat(response.getBody()).containsEntry("message", "Failed to upload");
    }

    @Test
    void handleExternalApi_shouldReturn502() {
        ExternalApiException ex = new ExternalApiException("Failed to fetch regions");

        ResponseEntity<Map<String, Object>> response = handler.handleExternalApi(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).containsEntry("status", 502);
        assertThat(response.getBody()).containsEntry("error", "Bad Gateway");
        assertThat(response.getBody()).containsEntry("message", "Failed to fetch regions");
    }

    @Test
    void handleRuntime_shouldReturn400() {
        RuntimeException ex = new RuntimeException("Artist not found");

        ResponseEntity<Map<String, Object>> response = handler.handleRuntime(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);
        assertThat(response.getBody()).containsEntry("error", "Bad Request");
        assertThat(response.getBody()).containsEntry("message", "Artist not found");
    }

    @Test
    void handleGeneral_shouldReturn500() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("status", 500);
        assertThat(response.getBody()).containsEntry("error", "Internal Server Error");
        assertThat(response.getBody()).containsEntry("message", "Unexpected error");
    }
}
