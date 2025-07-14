package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при невозможности найти карту с заданным id.
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Long cardId) {
        super("Карта с id=" + cardId + " не найдена.");
    }
}