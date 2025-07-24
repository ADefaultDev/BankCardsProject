package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при попытке выполнить операцию с несуществующим счетом.
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.1.0
 * @author Vsevolod Batyrov
 */
public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(Long userId) {
        super("Не существует счета для пользователя с id=" + userId);
    }
}
