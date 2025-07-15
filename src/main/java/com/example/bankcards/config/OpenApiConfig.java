package com.example.bankcards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация OpenAPI/Swagger для документации REST API.
 * <p>
 * Настраивает:
 * <ul>
 *   <li>Мета-информацию об API (название, версия, описание)</li>
 *   <li>Контактные данные разработчиков</li>
 *   <li>Лицензионную информацию</li>
 *   <li>Сервера для тестирования</li>
 * </ul>
 *
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification</a>
 * @since 1.0
 * @author Vsevolod Batyrov
 */
@Configuration
public class OpenApiConfig {

    @Value("${openapi.dev-url}")
    private String devUrl;

    @Value("${openapi.prod-url}")
    private String prodUrl;

    /**
     * Конфигурирует документацию OpenAPI.
     *
     * @return настроенный экземпляр {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url(devUrl).description("Development Server"),
                        new Server().url(prodUrl).description("Production Server")
                ))
                .info(new Info()
                        .title("Bank Cards API")
                        .version("1.0.0")
                        .description("""
                            ## REST API для системы банковских карт
                            ### Основные возможности:
                            - Управление пользователями
                            - Операции с банковскими картами
                            - Переводы между картами
                            - Аутентификация и авторизация
                            """)
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}