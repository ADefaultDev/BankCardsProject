package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с пользователями.
 * <p>
 * Предоставляет операции:
 * <ul>
 *   <li>Получение списка всех пользователей</li>
 *   <li>Удаление пользователей</li>
 * </ul>
 *
 * @see UserRepository Репозиторий для работы с базой данных
 * @see UserDTO DTO для передачи данных о пользователе
 * @since 1.0
 * @author Vsevolod Batyrov
*/

@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userRepository репозиторий для доступа к данным пользователей
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Возвращает список всех пользователей в системе.
     * Преобразует сущности {@link User} в {@link UserDTO} для безопасной передачи данных.
     *
     * @return список пользователей в формате DTO
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Удаляет пользователя по идентификатору.
     * Перед удалением проверяет существование пользователя.
     *
     * @param id идентификатор пользователя (не может быть null)
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     * @throws IllegalArgumentException если id == null
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Преобразует сущность User в UserDTO.
     * Скрывает конфиденциальные данные и преобразует роль в строковое представление.
     *
     * @param user сущность пользователя
     * @return DTO с основными данными пользователя
     * @throws IllegalArgumentException если user == null
     */
    private UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getSecondName(),
                user.getSurname(),
                user.getBirthday(),
                user.getRole().toString()
        );
    }
}
