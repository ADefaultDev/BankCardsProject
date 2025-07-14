package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.dto.TransferResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardInactiveException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.InvalidTransferAmountException;
import com.example.bankcards.exception.UnauthorizedCardAccessException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransferServiceTest {

    private CardRepository cardRepository;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        transferService = new TransferService(cardRepository);
    }

    @Test
    void transfer_ShouldSucceed_WhenValidRequest() {
        long userId = 1L;
        long fromCardId = 100L;
        long toCardId = 200L;

        Card fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(1000.0);
        fromCard.setUser(new User());
        fromCard.getUser().setId(userId);

        Card toCard = new Card();
        toCard.setId(toCardId);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(500.0);
        toCard.setUser(new User());
        toCard.getUser().setId(userId);

        TransferRequestDTO dto = new TransferRequestDTO(fromCardId, toCardId, 200.0);

        when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            TransferResponseDTO response = transferService.transfer(dto);

            assertEquals(fromCardId, response.fromCardId());
            assertEquals(toCardId, response.toCardId());
            assertEquals(200.0, response.amount());
            assertEquals("SUCCESS", response.status());
            assertEquals(800.0, fromCard.getBalance());
            assertEquals(700.0, toCard.getBalance());
        }

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void transfer_ShouldThrow_WhenAmountIsInvalid() {
        TransferRequestDTO dto = new TransferRequestDTO(1L, 2L, 0.0);

        assertThrows(InvalidTransferAmountException.class, () -> transferService.transfer(dto));
    }

    @Test
    void transfer_ShouldThrow_WhenFromCardNotFound() {
        TransferRequestDTO dto = new TransferRequestDTO(1L, 2L, 100.0);
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            assertThrows(CardNotFoundException.class, () -> transferService.transfer(dto));
        }
    }

    @Test
    void transfer_ShouldThrow_WhenUnauthorizedAccess() {
        long userId = 1L;

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(new User());
        fromCard.getUser().setId(99L);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(100.0);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(new User());
        toCard.getUser().setId(1L);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(0.0);

        TransferRequestDTO dto = new TransferRequestDTO(1L, 2L, 50.0);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            assertThrows(UnauthorizedCardAccessException.class, () -> transferService.transfer(dto));
        }
    }

    @Test
    void transfer_ShouldThrow_WhenCardInactive() {
        long userId = 1L;

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(new User());
        fromCard.getUser().setId(userId);
        fromCard.setStatus(CardStatus.BLOCKED);
        fromCard.setBalance(100.0);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(new User());
        toCard.getUser().setId(userId);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(0.0);

        TransferRequestDTO dto = new TransferRequestDTO(1L, 2L, 50.0);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            assertThrows(CardInactiveException.class, () -> transferService.transfer(dto));
        }
    }

    @Test
    void transfer_ShouldThrow_WhenInsufficientFunds() {
        long userId = 1L;

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(new User());
        fromCard.getUser().setId(userId);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(10.0);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(new User());
        toCard.getUser().setId(userId);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(0.0);

        TransferRequestDTO dto = new TransferRequestDTO(1L, 2L, 50.0);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            assertThrows(InsufficientFundsException.class, () -> transferService.transfer(dto));
        }
    }
}
