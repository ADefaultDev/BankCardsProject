package com.example.bankcards.util;

import java.util.Random;

/**
 * Генератор номеров банковских карт, соответствующих алгоритму Луна.
 * Генерирует 16-значные номера карт с указанным BIN (Bank Identification Number)
 * и корректной контрольной цифрой.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class CardNumberGenerator {

    private static final Random RANDOM = new Random();

    /**
     * Генерирует номер карты, соответствующий алгоритму Луна.
     *
     * @return сгенерированный номер карты в виде строки
     */
    public static String generateCardNumber() {
        String bin = "400000";
        StringBuilder number = new StringBuilder(bin);

        for (int i = 0; i < 9; i++) {
            number.append(RANDOM.nextInt(10));
        }

        int checkDigit = calculateLuhnDigit(number.toString());
        number.append(checkDigit);

        return number.toString();
    }

    /**
     * Вычисляет контрольную цифру по алгоритму Луна.
     *
     * @param number номер карты без контрольной цифры
     * @return контрольная цифра (0-9)
     */
    private static int calculateLuhnDigit(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
        }
        return (10 - (sum % 10)) % 10;
    }
}
