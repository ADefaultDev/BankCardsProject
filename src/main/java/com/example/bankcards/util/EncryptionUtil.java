package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Утилита для симметричного шифрования данных с использованием алгоритма AES.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "MySuperSecretKey";

    /**
     * Шифрует данные с использованием AES.
     *
     * @param data данные для шифрования
     * @return зашифрованная строка в Base64
     * @throws RuntimeException если произошла ошибка шифрования
     */
    public static String encrypt(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception ex) {
            throw new RuntimeException("Ошибка при шифровании данных", ex);
        }
    }

    /**
     * Расшифровывает данные, зашифрованные с помощью AES.
     *
     * @param encryptedData зашифрованная строка в Base64
     * @return расшифрованные данные
     * @throws RuntimeException если произошла ошибка расшифровки
     */
    public static String decrypt(String encryptedData) {

        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(original);

        } catch (Exception ex) {
            throw new RuntimeException("Ошибка при расшифровке данных", ex);
        }
    }
}
