package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.AccountNotFoundException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bankcards.util.CardNumberGenerator.generateCardNumber;
import static com.example.bankcards.util.SecurityUtils.getCurrentUserId;

/**
 * Сервис для работы с банковскими картами.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Создание новых карт</li>
 *   <li>Поиск карт пользователя</li>
 *   <li>Блокировка карты</li>
 *   <li>Маскировка номера карты</li>
 * </ul>
 *
 * @see CardRepository
 * @see CardDTO
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public CardService(CardRepository cardRepository,
                       UserRepository userRepository,
                       AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Создает новую банковскую карту для текущего пользователя.
     * Если у пользователя нет действующих карт, то весь баланс счета появится на ней.
     * В противном случае баланс будет равен 0.
     *
     * @return DTO созданной карты
     * @throws EntityNotFoundException если пользователь не найден
     * @throws AccountNotFoundException если у пользователя нет счета
     */
    public CardDTO createCard() {
        Long userId = getCurrentUserId();
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException(userId));

        Card card = new Card();
        card.setAccount(account);
        card.setEncryptedCardNumber(EncryptionUtil.encrypt(generateCardNumber()));
        card.setExpirationDate(LocalDate.now().plusYears(3));

        if(hasUserActiveCards(userId)){
            card.setBalance(0d);
        }else{
            card.setBalance(account.getBalance());
        }

        card.setStatus(CardStatus.ACTIVE);

        cardRepository.save(card);

        return toDTO(card);
    }

    /**
     * Возвращает список карт для текущего пользователя.
     *
     * @return List список карт
     */
    public List<CardDTO> getCardsForCurrentUser() {
        Long userId = getCurrentUserId();

        List<Card> cards = cardRepository.findByUserId(userId);

        if (cards.isEmpty()) {
            return Collections.emptyList();
        }

        return cards.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Блокирует карту по id.
     *
     * @param cardId id карты
     */
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    /**
     * Конвертирует сущность Card в CardDTO.
     *
     * @param card сущность Card для конвертации (не может быть null)
     * @return новый экземпляр CardDTO с маскированным номером
     * @throws IllegalArgumentException если card == null
     * @see #maskCardNumber(String)
     */
    public CardDTO toDTO(Card card) {
        String decryptedNumber = EncryptionUtil.decrypt(card.getEncryptedCardNumber());
        User currentUser = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        String displayNumber = currentUser.getRole().toString().equals("ROLE_USER")
                ? decryptedNumber
                : maskCardNumber(decryptedNumber);

        return new CardDTO(
                card.getId(),
                displayNumber,
                card.getAccount().getUser().getId(),
                card.getExpirationDate(),
                card.getBalance(),
                card.getStatus()
        );
    }

    /**
     * Маскирует номер карты, оставляя видимыми только последние 4 цифры.
     *
     * @param fullNumber полный номер карты (16 цифр, не может быть null или пустым)
     * @return маскированная строка номера карты
     */
    private String maskCardNumber(String fullNumber) {
        int length = fullNumber.length();
        String last4 = fullNumber.substring(length - 4);
        return "**** **** **** " + last4;
    }

    /**
     * Выводит баланс карты, если она принадлежит текущему пользователю.
     *
     * @param cardId id нужной карты
     * @return баланс карты
     */
    public Double getCardBalance(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        Long currentUserId = getCurrentUserId();
        if (!card.getAccount().getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException(cardId);
        }

        return card.getBalance();
    }

    /**
     * Возвращает все карты в системе для администратора (в маскированном виде)
     *
     * @return список карт
     */
    public List<CardDTO> getAllCardsForAdmin() {
        return cardRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, есть ли у пользователя активные карты.
     *
     * @param userId идентификатор пользователя (не может быть null)
     * @return true - если у пользователя есть хотя бы одна активная карта, иначе false
     * @throws IllegalArgumentException если userId равен null
     */
    public boolean hasUserActiveCards(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return cardRepository.findByUserId(userId)
                .stream()
                .anyMatch(card -> card.getStatus() == CardStatus.ACTIVE);
    }

}