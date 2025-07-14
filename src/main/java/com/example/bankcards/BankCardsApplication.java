package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Главный класс приложения для управления банковскими картами.
 * Включает:
 * - Автоконфигурацию Spring Boot
 * - Планировщик задач (@EnableScheduling)
 * - Конфигурацию кодировщика паролей
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@EnableScheduling
@SpringBootApplication
public class BankCardsApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(BankCardsApplication.class, args);
    }

    /**
     * Создает и настраивает кодировщик паролей BCrypt.
     *
     * @return экземпляр PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
