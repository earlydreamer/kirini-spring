package dev.earlydreamer.kirini.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                JwtUser jwtUser = jwtProvider.parseToken(token);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        jwtUser,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + jwtUser.authority().name()))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 유효하지 않은 토큰: 인증 미설정으로 진행
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}

