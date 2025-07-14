package com.example.bankcards.exception;


/**
 * Исключение, выбрасываемое при попытке выполнить получить доступ к чужой карте.
 * Наследуется от {@link RuntimeException}, поэтому является unchecked исключением.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(Long cardId) {
        super("Нет доступа к карте с id=" + cardId);
    }
}
