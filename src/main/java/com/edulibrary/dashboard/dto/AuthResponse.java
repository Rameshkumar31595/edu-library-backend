package com.edulibrary.dashboard.dto;

import java.time.Instant;

public record AuthResponse(String token, AuthUserResponse user, Instant expiresAt) {
}
