package dev.earlydreamer.kirini.security;

import dev.earlydreamer.kirini.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${security.jwt.secret:changeme-secret-key-base64}")
    private String secret;

    @Value("${security.jwt.expiration-ms:3600000}")
    private long expirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(Integer accountId, User.Authority authority) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(String.valueOf(accountId))
                .claim("auth", authority != null ? authority.name() : User.Authority.NORMAL.name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtUser parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Integer accountId = Integer.valueOf(claims.getSubject());
        String auth = claims.get("auth", String.class);
        return new JwtUser(accountId, User.Authority.valueOf(auth));
    }
}

