package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Кастомная реализация UserDetailsService для интеграции с Spring Security.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Загружает пользователя из базы данных по username</li>
 *   <li>Конвертирует сущность user в userDetails</li>
 *   <li>Обеспечивает механизм аутентификации через JPA</li>
 * </ul>
 *
 * @see UserDetailsService Стандартный интерфейс Spring Security
 * @see UserDetailsImpl Реализация UserDetails данного приложения
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userRepository репозиторий для работы с пользователями
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Загружает пользователя по username для Spring Security.
     *
     * @param username логин пользователя (не может быть null или пустым)
     * @return UserDetails с данными аутентификации
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return UserDetailsImpl.fromUser(user);
    }
}
