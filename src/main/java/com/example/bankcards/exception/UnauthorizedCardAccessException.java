package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при попытке отправить средства с карты,
 * или на карту, которая не принадлежит пользователю.
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class UnauthorizedCardAccessException extends RuntimeException {
    public UnauthorizedCardAccessException() {
        super("Вы можете переводить средства только между своими картами.");
    }
}