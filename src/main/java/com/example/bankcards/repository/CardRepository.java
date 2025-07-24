package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторий для работы с банковскими картами.
 * Предоставляет CRUD операции и специализированные методы поиска карт.
 * Наследует стандартные методы JpaRepository для сущности Card с идентификатором Long.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByStatus(CardStatus status);

    @Query("SELECT c FROM Card c JOIN c.account a WHERE a.user.id = :userId")
    List<Card> findByUserId(@Param("userId") Long userId);

    List<Card> findByAccountId(@Param("accountId") Long accountId);
}
