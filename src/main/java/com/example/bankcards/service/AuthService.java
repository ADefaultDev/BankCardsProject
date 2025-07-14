package com.example.bankcards.service;

import com.example.bankcards.dto.AuthResponseDTO;
import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.RegisterRequestDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UsernameAlreadyExistsException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с ауетинтификацией пользователя.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Регистрация пользователей</li>
 *   <li>Логин пользователя</li>
 * </ul>
 *
 * @see UserRepository
 * @see RegisterRequestDTO
 * @see LoginRequestDTO
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userRepository репозиторий для работы с пользователями
     * @param roleRepository репозиторий для работы с ролями
     * @param passwordEncoder кодировщик паролей
     * @param jwtTokenProvider генератор JWT-токенов
     * @param authenticationManager менеджер аутентификации Spring Security
     */
    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param dto объект с данными для входа (логин и пароль)
     * @return DTO с информацией о регистрации пользователя
     * @throws UsernameAlreadyExistsException если username уже есть в базе данных
     * @throws RuntimeException если роль USER не найдена в базе данных
     */
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new UsernameAlreadyExistsException(dto.username());
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));

        user.setFirstName(dto.firstName());
        user.setSecondName(dto.secondName());
        user.setSurname(dto.surname());
        user.setBirthday(dto.birthday());

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));
        user.setRole(userRole);

        userRepository.save(user);

        return new AuthResponseDTO("","Пользователь успешно зарегистрирован");
    }

    /**
     * Выполняет аутентификацию пользователя и генерирует JWT-токен.
     *
     * @param dto объект с данными для входа (логин и пароль)
     * @return DTO с JWT-токеном и сообщением об успехе
     * @throws UserNotFoundException если пользователя не существует в базе данных
     */
    public AuthResponseDTO login(LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new UserNotFoundException(dto.username()));

        String token = jwtTokenProvider.createToken(user.getUsername(),
                user.getRole() != null
                ? List.of(user.getRole().toString()) : List.of("USER"));

        return new AuthResponseDTO(token, "Вход успешен");
    }
}
