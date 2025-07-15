package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    private CardService cardService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = openMocks(this);
        cardService = new CardService(cardRepository, userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createCard_ShouldCreateCardSuccessfully() {
        long mockUserId = 1L;
        User mockUser = new User();
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        mockUser.setRole(role);
        mockUser.setId(mockUserId);

        try (MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class);
             MockedStatic<CardNumberGenerator> cardNumberMocked = mockStatic(CardNumberGenerator.class);
             MockedStatic<EncryptionUtil> encryptionMocked = mockStatic(EncryptionUtil.class)) {

            securityUtilsMocked.when(SecurityUtils::getCurrentUserId).thenReturn(mockUserId);
            cardNumberMocked.when(CardNumberGenerator::generateCardNumber).thenReturn("1234567812345678");
            encryptionMocked.when(() -> EncryptionUtil.encrypt("1234567812345678")).thenReturn("encrypted");
            encryptionMocked.when(() -> EncryptionUtil.decrypt("encrypted")).thenReturn("1234567812345678");

            when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));

            ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
            when(cardRepository.save(cardCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

            CardDTO result = cardService.createCard();

            assertNotNull(result);
            assertEquals(mockUserId, result.userId());
            assertTrue(result.cardNumber().startsWith("123456781234"));
            assertEquals(CardStatus.ACTIVE, result.status());
        }
    }

    @Test
    void createCard_ShouldThrowException_WhenUserNotFound() {
        long mockUserId = 99L;
        try (MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class)) {
            securityUtilsMocked.when(SecurityUtils::getCurrentUserId).thenReturn(mockUserId);
            when(userRepository.findById(mockUserId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> cardService.createCard());
        }
    }

    @Test
    void getCardsForCurrentUser_ShouldReturnCardList() {
        long mockUserId = 2L;
        User user = new User();
        user.setId(mockUserId);
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        user.setRole(role);

        Card card = new Card();
        card.setId(1L);
        card.setEncryptedCardNumber("enc");
        card.setUser(user);
        card.setExpirationDate(LocalDate.now());
        card.setBalance(100.0);
        card.setStatus(CardStatus.ACTIVE);

        List<Card> cardList = List.of(card);

        try (MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class);
             MockedStatic<EncryptionUtil> encryptionMocked = mockStatic(EncryptionUtil.class)) {

            securityUtilsMocked.when(SecurityUtils::getCurrentUserId).thenReturn(mockUserId);
            encryptionMocked.when(() -> EncryptionUtil.decrypt("enc")).thenReturn("1234567812345678");

            when(cardRepository.findByUserId(mockUserId)).thenReturn(cardList);
            when(userRepository.findById(mockUserId)).thenReturn(Optional.of(user));

            List<CardDTO> result = cardService.getCardsForCurrentUser();

            assertEquals(1, result.size());
        }
    }

    @Test
    void blockCard_ShouldChangeStatusToBlocked() {
        Long cardId = 10L;
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        cardService.blockCard(cardId);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void blockCard_ShouldThrowException_WhenCardNotFound() {
        Long cardId = 999L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.blockCard(cardId));
    }

    @Test
    void toDTO_ShouldConvertCorrectly() {
        User cardOwner = new User();
        cardOwner.setId(5L);
        Role role = new Role();
        role.setRoleName("ROLE_ADMIN");
        cardOwner.setRole(role);

        Card card = new Card();
        card.setId(1L);
        card.setUser(cardOwner);
        card.setEncryptedCardNumber("enc");
        card.setBalance(500.0);
        card.setExpirationDate(LocalDate.of(2030, 1, 1));
        card.setStatus(CardStatus.ACTIVE);

        try (MockedStatic<EncryptionUtil> encryptionMocked = mockStatic(EncryptionUtil.class);
             MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class)) {

            encryptionMocked.when(() -> EncryptionUtil.decrypt("enc")).thenReturn("1234567890123456");

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(cardOwner));

            securityUtilsMocked.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            User currentUser = new User();
            currentUser.setId(1L);
            currentUser.setRole(role);
            when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));

            CardDTO dto = cardService.toDTO(card);

            assertEquals("**** **** **** 3456", dto.cardNumber());

            assertEquals(CardStatus.ACTIVE, dto.status());
            assertEquals(5L, dto.userId());
        }
    }

    @Test
    void getCardBalance_ShouldReturnBalance_WhenOwnerCorrect() {
        Long cardId = 1L;
        Long currentUserId = 10L;

        User user = new User();
        user.setId(currentUserId);

        Card card = new Card();
        card.setId(cardId);
        card.setUser(user);
        card.setBalance(300.0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        try (MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class)) {
            securityUtilsMocked.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

            Double balance = cardService.getCardBalance(cardId);
            assertEquals(300.0, balance);
        }
    }

    @Test
    void getCardBalance_ShouldThrowAccessDenied_WhenUserNotOwner() {
        Long cardId = 1L;

        User user = new User();
        user.setId(999L);

        Card card = new Card();
        card.setId(cardId);
        card.setUser(user);
        card.setBalance(300.0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        try (MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class)) {
            securityUtilsMocked.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            assertThrows(AccessDeniedException.class, () -> cardService.getCardBalance(cardId));
        }
    }
}
