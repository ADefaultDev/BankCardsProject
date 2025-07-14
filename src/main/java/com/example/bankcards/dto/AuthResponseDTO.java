package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для ответа аутентификации.
 * @param token JWT-токен доступа (может быть null при ошибке)
 * @param message Информационное сообщение (например, об ошибке)
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "Ответ на запрос аутентификации")
public record AuthResponseDTO(

        @Schema(description = "JWT-токен доступа. Может быть null при ошибке", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Информационное сообщение", example = "Аутентификация прошла успешно")
        String message

) {}