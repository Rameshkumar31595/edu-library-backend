package com.edulibrary.dashboard.dto;

public record AuthUserResponse(Long id, String name, String email, String role, String studentEmail) {
}
