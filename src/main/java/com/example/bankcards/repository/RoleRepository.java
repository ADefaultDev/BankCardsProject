package com.example.bankcards.repository;

import com.example.bankcards.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с ролями пользователя.
 * Наследует стандартные методы JpaRepository для сущности Role с идентификатором Long.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
