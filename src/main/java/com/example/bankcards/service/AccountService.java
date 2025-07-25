package com.example.bankcards.service;

import com.example.bankcards.dto.AccountDTO;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccountNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.UserRepository;

import static com.example.bankcards.util.SecurityUtils.getCurrentUserId;

/**
 * Сервис для работы со счетами пользователей.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Создание счета</li>
 *   <li>Просмотр баланса счета</li>
 *   <li>Удаление счета(только для администратора)</li>
 * </ul>
 *
 * @see AccountRepository
 * @since 1.1.0
 * @author Vsevolod Batyrov
 */
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param accountRepository репозиторий для работы со счетами пользователя.
     */
    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    /**
     * Создает новый банковский счет для текущего пользователя.
     *
     * @return DTO созданной карты
     * @throws UserNotFoundException если пользователь не найден
     */
    public AccountDTO createAccount() {
        Long userId = getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Account account = new Account();
        account.setUser(user);
        account.setBalance(0d);

        accountRepository.save(account);

        return toDTO(account);
    }

    /**
     * Выводит текущий баланс на счету.
     *
     * @return DTO созданной карты
     * @throws UserNotFoundException если пользователь не найден
     */
    public Double checkAccount() {
        Long userId = getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException(userId));

        return account.getBalance();

    }

    /**
     * Удаляет банковский счет по идентификатору.
     * Перед удалением проверяет существование пользователя.
     *
     * @param id идентификатор пользователя (не может быть null)
     * @throws AccountNotFoundException если счет с указанным ID не найден
     * @throws IllegalArgumentException если id == null
     */
    public void deleteAccount(Long id) {
        if(!accountRepository.existsById(id)){
            throw new AccountNotFoundException(id);
        }

        accountRepository.deleteById(id);
    }

    /**
     * Конвертирует сущность Account в AccountDTO.
     *
     * @param account сущность Account для конвертации (не может быть null)
     * @return новый экземпляр AccountDTO
     * @throws IllegalArgumentException если account == null
     */
    public AccountDTO toDTO(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getUser().getId(),
                account.getBalance()
        );
    }

}
