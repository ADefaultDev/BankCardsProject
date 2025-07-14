package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для проверки и обработки просроченных банковских карт.
 * Выполняет автоматическую проверку карт по расписанию.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Service
public class CardExpirationChecker {

    private final CardRepository cardRepository;

    /**
     * Конструктор с внедрением зависимости CardRepository.
     *
     * @param cardRepository репозиторий для работы с картами
     */
    public CardExpirationChecker(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Проверяет и помечает просроченные карты как EXPIRED.
     * Запускается ежедневно в полночь по cron-расписанию.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndExpireCards() {
        List<Card> activeCards = cardRepository.findByStatus(CardStatus.ACTIVE);
        LocalDate today = LocalDate.now();

        for (Card card : activeCards) {
            if (card.getExpirationDate() != null && card.getExpirationDate().isBefore(today)) {
                card.setStatus(CardStatus.EXPIRED);
                cardRepository.save(card);
            }
        }
    }
}
