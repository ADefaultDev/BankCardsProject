package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO для запроса перевода между картами.
 * @param fromCardId ID карты-отправителя (обязательное)
 * @param toCardId ID карты-получателя (обязательное)
 * @param amount Сумма перевода (положительная)
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "Запрос на перевод средств между картами")
public record TransferRequestDTO(

        @Schema(description = "ID карты-отправителя", example = "1001")
        @NotNull(message = "Идентификатор карты отправителя не может быть пустым")
        Long fromCardId,

        @Schema(description = "ID карты-получателя", example = "2002")
        @NotNull(message = "Идентификатор карты получателя не может быть пустым")
        Long toCardId,

        @Schema(description = "Сумма перевода (положительное число)", example = "250.00")
        @NotNull(message = "Сумма перевода не может быть пустой")
        @Positive(message = "Сумма перевода должна быть больше нуля")
        Double amount

) {}