package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountDTO;
import com.example.bankcards.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки запросов аутентификации и регистрации пользователей.
 * Предоставляет API для регистрации новых пользователей и входа в систему.
 * @since 1.1.0
 * @author Vsevolod Batyrov
 */
@RestController
@RequestMapping("/api/accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    /**
     * Конструктор для внедрения зависимости AccountService.
     *
     * @param accountService сервис для выполнения операций перевода
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Создает новую банковский счет для текущего пользователя.
     * Доступен только аутентифицированным пользователям с ролью USER.
     *
     * @return ResponseEntity с созданным счетом
     */
    @Operation(summary = "Создание нового банковского счета")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счет успешно создан"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не USER)")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountDTO> create() {
        return ResponseEntity.ok(accountService.createAccount());
    }

    /**
     * Выводит баланс на счете пользователя.
     * Доступен только аутентифицированным пользователям с ролью USER.
     *
     * @return ResponseEntity с созданным счетом
     */
    @Operation(summary = "Проверка баланса счета")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не USER)")
    })
    @GetMapping("/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Double> getBalance() {
        return ResponseEntity.ok(accountService.checkAccount());
    }

    /**
     * Удаляет счет пользователя.
     * Доступен только аутентифицированным пользователям с ролью ADMIN.
     *
     * @param id идентификатор банковского счета
     * @return пустой ResponseEntity
     */
    @Operation(summary = "Удаление банковского счета по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счет успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Счет не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (не ADMIN)"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
