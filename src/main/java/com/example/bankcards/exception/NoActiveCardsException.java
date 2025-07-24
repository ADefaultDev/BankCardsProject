package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при попытке выполнить операцию со счетом без активных карт.
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.1.0
 * @author Vsevolod Batyrov
 */
public class NoActiveCardsException extends RuntimeException {
    public NoActiveCardsException(Long accountId) {
        super("Для аккаунта с id=" + accountId + " не найдено активных карт");
    }
}
