package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO для запроса перевода между картами.
 * @param fromAccountId ID счета отправителя (обязательное)
 * @param toAccountId ID счета получателя (обязательное)
 * @param amount Сумма перевода (положительная)
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Schema(description = "Запрос на перевод средств между счетами")
public record TransferRequestDTO(

        @Schema(description = "ID счета отправителя", example = "1001")
        @NotNull(message = "Идентификатор счета отправителя не может быть пустым")
        Long fromAccountId,

        @Schema(description = "ID карты-получателя", example = "2002")
        @NotNull(message = "Идентификатор карты получателя не может быть пустым")
        Long toAccountId,

        @Schema(description = "Сумма перевода (положительное число)", example = "250.00")
        @NotNull(message = "Сумма перевода не может быть пустой")
        @Positive(message = "Сумма перевода должна быть больше нуля")
        Double amount

) {}