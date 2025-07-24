package com.example.bankcards.repository;

import com.example.bankcards.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с банковскими счетами.
 * Предоставляет CRUD операции и специализированные методы поиска счета.
 * Наследует стандартные методы JpaRepository для сущности Account с идентификатором Long.
 * @since 1.1.0
 * @author Vsevolod Batyrov
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);
}
