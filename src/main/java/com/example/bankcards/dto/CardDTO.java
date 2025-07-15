package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO, представляющий данные банковской карты.
 * @param id Уникальный идентификатор карты
 * @param cardNumber  номер карты (может быть замаскирован)
 * @param userId Идентификатор владельца карты
 * @param expirationDate Дата истечения срока действия
 * @param balance Текущий баланс (используется BigDecimal для точности)
 * @param status Статус карты (ACTIVE, BLOCKED, EXPIRED)
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "DTO, представляющий данные банковской карты")
public record CardDTO(

        @Schema(description = "Уникальный идентификатор карты", example = "101")
        Long id,

        @Schema(description = "Замаскированный номер карты", example = "****1234")
        String cardNumber,

        @Schema(description = "Идентификатор владельца карты", example = "202")
        Long userId,

        @Schema(description = "Дата истечения срока действия", example = "2026-12-31")
        LocalDate expirationDate,

        @Schema(description = "Текущий баланс", example = "1500.75")
        Double balance,

        @Schema(description = "Статус карты (ACTIVE, BLOCKED, EXPIRED)", example = "ACTIVE")
        CardStatus status

) {}