package dev.earlydreamer.kirini.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private long expiresIn;
}

