package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.dto.TransferResponseDTO;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.AccountNotFoundException;
import com.example.bankcards.exception.CardInactiveException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.InvalidTransferAmountException;
import com.example.bankcards.exception.UnauthorizedCardAccessException;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
 * @see AccountRepository
 * @see CardRepository
 * @see TransferRequestDTO
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    public TransferService(AccountRepository accountRepository,
                           CardRepository cardRepository) {
        this.accountRepository = accountRepository;
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
     *            fromAccountId, toAccountId и amount (не может быть null)
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

        Account fromAccount = accountRepository.findByUserId(dto.fromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(dto.fromAccountId()));

        Account toAccount = accountRepository.findByUserId(dto.toAccountId())
                .orElseThrow(() -> new AccountNotFoundException(dto.toAccountId()));

        if (!fromAccount.getUser().getId().equals(currentUserId)) {
            throw new UnauthorizedCardAccessException();
        }

        if (fromAccount.getBalance() < dto.amount()) {
            throw new InsufficientFundsException();
        }

        List<Card> fromCards = cardRepository.findByAccountId(fromAccount.getId());
        List<Card> toCards = cardRepository.findByAccountId(fromAccount.getId());

        Card fromCard = null;
        Card toCard = null;

        for(Card card : fromCards){
            if(card.getStatus() == CardStatus.ACTIVE){
                fromCard = card;
                break;
            }
        }

        if(fromCard == null) {
            throw new CardInactiveException(fromAccount.getId());
        }

        for(Card card : toCards){
            if(card.getStatus() == CardStatus.ACTIVE){
                toCard = card;
                break;
            }
        }

        if(toCard == null) {
            throw new CardInactiveException(toAccount.getId());
        }

        double transferAmount = dto.amount();

        for(Card card : fromCards){
            if(card.getStatus() == CardStatus.ACTIVE && card.getBalance() > 0){
                fromCard = card;
                if(fromCard.getBalance() >= transferAmount){
                    fromCard.setBalance(fromCard.getBalance() - transferAmount);
                    transferAmount-=transferAmount;
                    cardRepository.save(fromCard);
                }else{
                    transferAmount-=fromCard.getBalance();
                    fromCard.setBalance(0d);
                    cardRepository.save(fromCard);
                }
            }
            if(transferAmount == 0) break;
        }

        toCard.setBalance(toCard.getBalance() + dto.amount());

        cardRepository.save(toCard);

        fromAccount.setBalance(fromAccount.getBalance() - dto.amount());
        toAccount.setBalance(toAccount.getBalance() + dto.amount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return new TransferResponseDTO(
                dto.fromAccountId(),
                dto.toAccountId(),
                dto.amount(),
                LocalDateTime.now(),
                "SUCCESS"
        );
    }

}
