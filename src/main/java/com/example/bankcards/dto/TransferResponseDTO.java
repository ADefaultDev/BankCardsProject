package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO для ответа о переводе средств между картами
 * @param fromCardId ID карты отправителя
 * @param toCardId ID карты получателя
 * @param amount Сумма перевода
 * @param timestamp Время выполнения операции
 * @param status Статус перевода ("SUCCESS", "FAILED" и т.д.)
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "Ответ на перевод средств между картами")
public record TransferResponseDTO(

        @Schema(description = "ID карты отправителя", example = "1001")
        Long fromCardId,

        @Schema(description = "ID карты получателя", example = "2002")
        Long toCardId,

        @Schema(description = "Сумма перевода", example = "250.00")
        Double amount,

        @Schema(description = "Дата и время выполнения операции", example = "2025-07-14T12:45:00")
        LocalDateTime timestamp,

        @Schema(description = "Статус перевода", example = "SUCCESS")
        String status

) {}