package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при попытке отправить средства с карты,
 * на которой не хватает средств
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Недостаточно средств для перевода.");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}