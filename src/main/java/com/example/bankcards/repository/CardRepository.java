package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Репозиторий для работы с банковскими картами.
 * Предоставляет CRUD операции и специализированные методы поиска карт.
 * Наследует стандартные методы JpaRepository для сущности Card с идентификатором Long.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByUserId(Long userId);
    List<Card> findByStatus(CardStatus status);
}
