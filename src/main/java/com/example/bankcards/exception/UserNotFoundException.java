package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при попытке обратиться к несуществующему пользователю.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super("Пользователь с username" + username + " не найден.");
    }
    public UserNotFoundException(Long id) {
        super("Пользователь с id" + id + " не найден.");
    }
}