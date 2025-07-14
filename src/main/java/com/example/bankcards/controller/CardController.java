package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления банковскими картами.
 * Предоставляет API для создания, просмотра и блокировки карт.
 * Требует аутентификации через JWT токен.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@RestController
@RequestMapping("/api/cards")
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService cardService;

    /**
     * Конструктор для внедрения зависимости CardService.
     *
     * @param cardService сервис для работы с банковскими картами
     */
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }
    /**
     * Создает новую банковскую карту для текущего пользователя.
     * Доступен только аутентифицированным пользователям с ролью USER.
     *
     * @return ResponseEntity с созданной картой
     */
    @Operation(summary = "Создание новой банковской карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не USER)")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardDTO> create() {
        return ResponseEntity.ok(cardService.createCard());
    }

    /**
     * Получает список всех карт текущего пользователя.
     * Доступен только аутентифицированным пользователям с ролью USER.
     *
     * @return ResponseEntity со списком карт пользователя
     */
    @Operation(summary = "Получение всех карт текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не USER)")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardDTO>> getCardsForUser() {
        return ResponseEntity.ok(cardService.getCardsForCurrentUser());
    }

    /**
     * Получает баланс конкретной карты текущего пользователя.
     * Доступен только аутентифицированным пользователям с ролью USER.
     *
     * @param id идентификатор карты
     * @return ResponseEntity с балансом карты
     */
    @Operation(summary = "Получение баланса карты по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс карты успешно получен"),
            @ApiResponse(responseCode = "400", description = "Карта не найдена или недоступна"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не USER)")
    })
    @GetMapping("/{id}/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Double> getCardBalance(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardBalance(id));
    }

    /**
     * Блокирует указанную карту.
     * Доступен только аутентифицированным пользователям с ролью ADMIN.
     *
     * @param id идентификатор карты для блокировки
     * @return ResponseEntity с сообщением об успешной блокировке
     */
    @Operation(summary = "Блокировка карты по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована"),
            @ApiResponse(responseCode = "400", description = "Карта не найдена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не ADMIN)")
    })
    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> blockCard(@PathVariable Long id) {
        cardService.blockCard(id);
        return ResponseEntity.ok("Card blocked successfully");
    }
}