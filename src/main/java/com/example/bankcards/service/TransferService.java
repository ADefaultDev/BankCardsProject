package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.dto.TransferResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardInactiveException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.InvalidTransferAmountException;
import com.example.bankcards.exception.UnauthorizedCardAccessException;
import com.example.bankcards.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.bankcards.util.SecurityUtils.getCurrentUserId;

/**
 * Сервис для перевода средств между картами пользователя.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Перевод средств между картами</li>
 *   <li>Проверка перевода на корректность данных</li>
 * </ul>
 *
 * @see CardRepository
 * @see TransferRequestDTO
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Service
public class TransferService {

    private final CardRepository cardRepository;

    public TransferService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Выполняет перевод средств между картами пользователя.
     * <p>
     * Процесс выполнения перевода:
     * <ol>
     *   <li>Проверка валидности суммы перевода</li>
     *   <li>Поиск карт отправителя и получателя</li>
     *   <li>Проверка прав доступа к картам</li>
     *   <li>Проверка активности карт</li>
     *   <li>Проверка достаточности средств</li>
     *   <li>Выполнение транзакции</li>
     * </ol>
     *
     * @param dto объект {@link TransferRequestDTO} с данными перевода:
     *            fromCardId, toCardId и amount (не может быть null)
     * @return {@link TransferResponseDTO} с результатом операции
     * @throws InvalidTransferAmountException если сумма перевода некорректна
     * @throws CardNotFoundException если карта не найдена
     * @throws UnauthorizedCardAccessException если нет доступа к карте
     * @throws CardInactiveException если карта неактивна
     * @throws InsufficientFundsException если недостаточно средств
     */
    @Transactional
    public TransferResponseDTO transfer(TransferRequestDTO dto) {
        if (dto.amount() == null || dto.amount() <= 0) {
            throw new InvalidTransferAmountException();
        }

        Long currentUserId = getCurrentUserId();

        Card fromCard = cardRepository.findById(dto.fromCardId())
                .orElseThrow(() -> new CardNotFoundException(dto.fromCardId()));

        Card toCard = cardRepository.findById(dto.toCardId())
                .orElseThrow(() -> new CardNotFoundException(dto.toCardId()));

        if (!fromCard.getUser().getId().equals(currentUserId) || !toCard.getUser().getId().equals(currentUserId)) {
            throw new UnauthorizedCardAccessException();
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new CardInactiveException(fromCard.getId());
        }

        if (toCard.getStatus() != CardStatus.ACTIVE){
            throw new CardInactiveException(toCard.getId());
        }

        if (fromCard.getBalance() < dto.amount()) {
            throw new InsufficientFundsException();
        }

        fromCard.setBalance(fromCard.getBalance() - dto.amount());
        toCard.setBalance(toCard.getBalance() + dto.amount());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        return new TransferResponseDTO(
                dto.fromCardId(),
                dto.toCardId(),
                dto.amount(),
                LocalDateTime.now(),
                "SUCCESS"
        );
    }

}
