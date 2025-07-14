package com.example.bankcards.exception;

/**
 * Исключение, выбрасываемое при попытке выполнить операцию с неактивной картой.
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class CardInactiveException extends RuntimeException {
    public CardInactiveException(Long cardId) {
        super("Карта с id=" + cardId + " неактивна");
    }
}