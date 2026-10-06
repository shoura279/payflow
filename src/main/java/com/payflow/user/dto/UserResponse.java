package com.payflow.user.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        com.payflow.user.entity.UserStatus status
) {
}