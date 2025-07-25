package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO, представляющий данные банковской карты.
 *
 * @param id Уникальный идентификатор карты
 * @param userId Уникальный идентификатор владельца счета
 * @param balance Текущий баланс (используется BigDecimal для точности)
 * @since 1.1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "DTO, представляющий данные банковского счета")
public record AccountDTO(

        @Schema(description = "Уникальный идентификатор счета", example = "1")
        Long id,

        @Schema(description = "Уникальный идентификатор владельца счета", example = "2")
        Long userId,

        @Schema(description = "Текущий баланс счета", example = "1234")
        Double balance

) {}