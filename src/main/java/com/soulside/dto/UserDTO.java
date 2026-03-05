package com.soulside.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "User ID is required") String id,
        @NotBlank(message = "User name is required") String name) {
}
