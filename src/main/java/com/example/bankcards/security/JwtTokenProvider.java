package com.example.bankcards.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Компонент для работы с JWT токенами.
 * Обеспечивает создание, валидацию и извлечение данных из JWT токенов.
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expirationMs}")
    private long validityInMillis;

    private SecretKey secretKey;

    /**
     * Инициализирует секретный ключ на основе base64-encoded строки из конфигурации.
     * Вызывается автоматически после создания бина.
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Создает JWT токен для пользователя.
     *
     * @param username имя пользователя
     * @param roles список ролей пользователя
     * @return сгенерированный JWT токен
     */
    public String createToken(String username, List<String> roles) {

        Date now = new Date();

        Date expiry = new Date(now.getTime() + validityInMillis);

        return Jwts.builder()
                .claim("sub", username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();

    }

    /**
     * Извлекает имя пользователя из JWT токена.
     *
     * @param token JWT токен
     * @return имя пользователя
     * @throws io.jsonwebtoken.JwtException если токен невалидный
     */
    public String getUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("sub", String.class);

    }

    /**
     * Проверяет валидность JWT токена.
     *
     * @param token JWT токен для проверки
     * @return true если токен валиден, false в противном случае
     */
    public boolean validateToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }

    }
}
