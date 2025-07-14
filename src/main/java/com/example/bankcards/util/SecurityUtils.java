package com.example.bankcards.util;

import com.example.bankcards.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Утилитный класс для работы с контекстом безопасности Spring Security.
 * Предоставляет методы для получения информации о текущем аутентифицированном пользователе.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Возвращает ID текущего аутентифицированного пользователя.
     *
     * @return ID пользователя
     * @throws IllegalStateException если пользователь не аутентифицирован
     * или principal имеет неверный тип
     */
    public static Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        return userDetails.id();
    }
}
