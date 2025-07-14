package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при попытке отправить неверную сумму с карты.
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class InvalidTransferAmountException extends RuntimeException {
    public InvalidTransferAmountException() {
        super("Некорректная сумма перевода. Сумма должна быть положительным числом.");
    }
}