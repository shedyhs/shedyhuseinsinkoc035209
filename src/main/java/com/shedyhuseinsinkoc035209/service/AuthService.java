package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.dto.LoginRequest;
import com.shedyhuseinsinkoc035209.dto.LoginResponse;
import com.shedyhuseinsinkoc035209.dto.RefreshRequest;
import com.shedyhuseinsinkoc035209.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String username = authentication.getName();
        String accessToken = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        return new LoginResponse(accessToken, refreshToken, jwtUtil.getExpiration(), "Bearer");
    }

    public LoginResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username);

        return new LoginResponse(newAccessToken, refreshToken, jwtUtil.getExpiration(), "Bearer");
    }
}
