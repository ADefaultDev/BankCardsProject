package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.dto.TransferResponseDTO;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки операций перевода средств между картами.
 * Требует аутентификации через JWT токен.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@RestController
@RequestMapping("/api/transfers")
@SecurityRequirement(name = "bearerAuth")
public class TransferController {

    private final TransferService transferService;

    /**
     * Конструктор для внедрения зависимости TransferService.
     *
     * @param transferService сервис для выполнения операций перевода
     */
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Выполняет перевод средств между картами.
     * Доступен только аутентифицированным пользователям с ролью USER.
     *
     * @param dto объект запроса на перевод с валидируемыми полями
     * @return ResponseEntity с результатом выполнения перевода
     */
    @Operation(summary = "Перевод средств между картами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации, недостаточно средств, карта не найдена или заблокирована"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не USER)"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransferResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO dto) {
        TransferResponseDTO response = transferService.transfer(dto);
        return ResponseEntity.ok(response);
    }
}