package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO для запроса регистрации пользователя.
 * @param username Логин (4-20 символов)
 * @param password Пароль (минимум 8 символов)
 * @param firstName Имя (только буквы и дефисы)
 * @param secondName Фамилия (только буквы и дефисы)
 * @param surname Отчество (только буквы и дефисы)
 * @param birthday Дата рождения (в прошлом)
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "Запрос на регистрацию нового пользователя")
public record RegisterRequestDTO(

        @Schema(description = "Логин (4–20 символов)", example = "newuser123")
        @NotBlank(message = "Логин не может быть пустым")
        @Size(min = 4, max = 20, message = "Логин должен быть от 4 до 20 символов")
        String username,

        @Schema(description = "Пароль (от 8 до 30 символов)", example = "password123")
        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 30, message = "Пароль должен содержать минимум 8 символов")
        String password,

        @Schema(description = "Имя (только буквы и дефисы)", example = "Иван")
        @NotBlank(message = "Имя не может быть пустым")
        @Pattern(regexp = "^[\\p{L}-]+$", message = "Имя должно содержать только буквы и дефисы")
        String firstName,

        @Schema(description = "Фамилия (только буквы и дефисы)", example = "Петров")
        @NotBlank(message = "Фамилия не может быть пустой")
        @Pattern(regexp = "^[\\p{L}-]+$", message = "Фамилия должна содержать только буквы и дефисы")
        String secondName,

        @Schema(description = "Отчество (необязательно, только буквы и дефисы)", example = "Александрович")
        @Pattern(regexp = "^[\\p{L}-]+$", message = "Отчество должно содержать только буквы и дефисы")
        String surname,

        @Schema(description = "Дата рождения (должна быть в прошлом)", example = "1990-05-20")
        @Past(message = "День рождения должен быть в прошлом")
        LocalDate birthday

) {}
