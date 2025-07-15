package com.example.bankcards.exception;

/**
 * Класс для стандартизированного представления ошибок в API.
 * Содержит сообщение об ошибке и HTTP статус код.
 * Используется для возврата клиенту единого формата ошибок.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class ErrorResponse {

    private String message;

    private int status;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
