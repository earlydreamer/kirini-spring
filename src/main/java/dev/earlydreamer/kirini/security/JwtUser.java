package dev.earlydreamer.kirini.security;

import dev.earlydreamer.kirini.domain.User;

public record JwtUser(Integer accountId, User.Authority authority) {
}

