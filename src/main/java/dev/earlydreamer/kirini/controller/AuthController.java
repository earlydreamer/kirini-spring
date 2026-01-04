package dev.earlydreamer.kirini.controller;

import dev.earlydreamer.kirini.domain.User;
import dev.earlydreamer.kirini.dto.request.LoginRequest;
import dev.earlydreamer.kirini.dto.request.RefreshTokenRequest;
import dev.earlydreamer.kirini.dto.response.ApiResponse;
import dev.earlydreamer.kirini.dto.response.TokenResponse;
import dev.earlydreamer.kirini.security.JwtProvider;
import dev.earlydreamer.kirini.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        User user = principal.getUser();
        String token = jwtProvider.generateToken(user.getId(), user.getAuthority());
        TokenResponse response = TokenResponse.builder()
                .accessToken(token)
                .expiresIn(jwtProvider.getExpirationMs())
                .build();
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공했습니다.", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String token = jwtProvider.refresh(request.getToken());
        TokenResponse response = TokenResponse.builder()
                .accessToken(token)
                .expiresIn(jwtProvider.getExpirationMs())
                .build();
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", response));
    }
}
