package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthResponseDTO;
import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.RegisterRequestDTO;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки запросов аутентификации и регистрации пользователей.
 * Предоставляет API для регистрации новых пользователей и входа в систему.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Конструктор с внедрением зависимости AuthService.
     *
     * @param authService сервис для обработки логики аутентификации
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Обрабатывает запрос на регистрацию нового пользователя.
     *
     * @param dto объект запроса регистрации с валидируемыми полями
     * @return ResponseEntity с AuthResponseDTO, содержащим JWT токен
     */
    @Operation(
            summary = "Регистрация нового пользователя",
            requestBody = @RequestBody(
                    required = true,
                    description = "Данные нового пользователя",
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequestDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                  "username": "new_user",
                                  "password": "password123",
                                  "firstName": "Иван",
                                  "secondName": "Иванов",
                                  "surname": "Иванович",
                                  "birthday": "1990-01-01"
                                }
                                """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO dto) {
        AuthResponseDTO response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Обрабатывает запрос на вход пользователя в систему.
     *
     * @param dto объект запроса входа с валидируемыми полями
     * @return ResponseEntity с AuthResponseDTO, содержащим JWT токен
     */
    @Operation(
            summary = "Аутентификация пользователя (вход)",
            requestBody = @RequestBody(
                    required = true,
                    description = "Данные для входа",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequestDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                  "username": "user123",
                                  "password": "pass1234"
                                }
                                """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аутентификация успешна",
                            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
