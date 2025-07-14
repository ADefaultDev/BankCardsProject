package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при попытке зарегистрироваться под занятым логином.
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Логин '" + username + "' уже занят.");
    }
}