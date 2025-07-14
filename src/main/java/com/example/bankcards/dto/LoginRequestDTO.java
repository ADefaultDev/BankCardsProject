package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для запроса аутентификации.
 * @param username Логин пользователя (4-20 символов)
 * @param password Пароль (минимум 6 символов)
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "Запрос на аутентификацию пользователя")
public record LoginRequestDTO(

        @Schema(description = "Логин пользователя (4–20 символов)", example = "user123")
        @NotBlank(message = "Логин не может быть пустым")
        @Size(min = 4, max = 20, message = "Логин должен быть от 4 до 20 символов")
        String username,

        @Schema(description = "Пароль пользователя (6-30 символов)", example = "password123")
        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 6, max = 30, message = "Пароль должен быть не менее 6 символов")
        String password

) {}