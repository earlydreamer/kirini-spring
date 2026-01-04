package dev.earlydreamer.kirini.controller;

import dev.earlydreamer.kirini.domain.User;
import dev.earlydreamer.kirini.dto.request.LoginRequest;
import dev.earlydreamer.kirini.dto.response.ApiResponse;
import dev.earlydreamer.kirini.dto.response.TokenResponse;
import dev.earlydreamer.kirini.security.JwtProvider;
import dev.earlydreamer.kirini.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
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
}

