package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CardExpirationCheckerTest {

    private CardRepository cardRepository;
    private CardExpirationChecker checker;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        checker = new CardExpirationChecker(cardRepository);
    }

    @Test
    void checkAndExpireCards_ShouldExpireCardsPastDueDate() {
        Card expiredCard1 = new Card();
        expiredCard1.setId(1L);
        expiredCard1.setStatus(CardStatus.ACTIVE);
        expiredCard1.setExpirationDate(LocalDate.now().minusDays(1));

        Card expiredCard2 = new Card();
        expiredCard2.setId(2L);
        expiredCard2.setStatus(CardStatus.ACTIVE);
        expiredCard2.setExpirationDate(LocalDate.now().minusYears(1));

        Card validCard = new Card();
        validCard.setId(3L);
        validCard.setStatus(CardStatus.ACTIVE);
        validCard.setExpirationDate(LocalDate.now().plusDays(30));

        when(cardRepository.findByStatus(CardStatus.ACTIVE))
                .thenReturn(List.of(expiredCard1, expiredCard2, validCard));

        checker.checkAndExpireCards();

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository, times(2)).save(captor.capture());

        List<Card> savedCards = captor.getAllValues();
        assertEquals(2, savedCards.size());

        for (Card savedCard : savedCards) {
            assertEquals(CardStatus.EXPIRED, savedCard.getStatus());
        }

        verify(cardRepository, never()).save(validCard);
    }

    @Test
    void checkAndExpireCards_ShouldSkipCardsWithoutExpirationDate() {
        Card cardWithoutDate = new Card();
        cardWithoutDate.setId(4L);
        cardWithoutDate.setStatus(CardStatus.ACTIVE);
        cardWithoutDate.setExpirationDate(null);

        when(cardRepository.findByStatus(CardStatus.ACTIVE))
                .thenReturn(List.of(cardWithoutDate));

        checker.checkAndExpireCards();

        verify(cardRepository, never()).save(any());
    }
}
