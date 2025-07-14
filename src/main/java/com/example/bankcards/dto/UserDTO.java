package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO для представления данных пользователя.
 * @param id Уникальный идентификатор пользователя
 * @param username Логин пользователя (уникальный)
 * @param firstName Имя
 * @param secondName Фамилия
 * @param surname Отчество (может быть null)
 * @param birthday Дата рождения (не может быть будущей датой)
 * @param roleName Название роли пользователя
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "Данные пользователя")
public record UserDTO(

        @Schema(description = "Уникальный идентификатор пользователя", example = "123")
        Long id,

        @Schema(description = "Логин пользователя (уникальный)", example = "user123")
        String username,

        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String secondName,

        @Schema(description = "Отчество пользователя", example = "Иванович", nullable = true)
        String surname,

        @Schema(description = "Дата рождения (не может быть будущей датой)", example = "1980-05-20", type = "string", format = "date")
        LocalDate birthday,

        @Schema(description = "Название роли пользователя", example = "USER")
        String roleName

) {}