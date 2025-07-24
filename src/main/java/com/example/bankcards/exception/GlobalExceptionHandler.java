package com.example.bankcards.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST API.
 * Обрабатывает все исключения, возникающие в контроллерах,
 * и возвращает стандартизированные ответы с ошибками.
 *
 * <p>Аннотация @Hidden скрывает этот класс из Swagger документации.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@ControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(CardInactiveException.class)
    public ResponseEntity<ErrorResponse> handleCardInactive(CardInactiveException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFound(CardNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidTransferAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidTransferAmountException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UnauthorizedCardAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedCardAccess(UnauthorizedCardAccessException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Обрабатывает все неперехваченные исключения.
     *
     * @param ex исключение Exception
     * @return ResponseEntity с сообщением об ошибке и статусом INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoActiveCards(NoActiveCardsException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Internal server error");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает ошибки валидации входных данных.
     *
     * @param ex исключение MethodArgumentNotValidException
     * @return ResponseEntity с детализированным сообщением об ошибках валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            sb.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }

        String details = sb.toString().trim();

        if (details.endsWith(";")) {
            details = details.substring(0, details.length() - 1);
        }

        ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации", HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(details);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает ошибки нечитаемого HTTP сообщения.
     *
     * @param ex исключение HttpMessageNotReadableException
     * @return ResponseEntity с сообщением об ошибке формата данных
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormatException(HttpMessageNotReadableException ex) {
        String message = "Некорректный формат данных";

        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            String fieldName = "";
            if (!invalidFormatException.getPath().isEmpty()) {
                fieldName = invalidFormatException.getPath().getFirst().getFieldName();
            }
            message = "Некорректное значение поля '" + fieldName + "'";
        }

        ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
