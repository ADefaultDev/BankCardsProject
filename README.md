
# BankCards REST API

RESTful-сервис для управления банковскими картами, пользователями и переводами между ними. Реализован с использованием Java 17, Spring Boot, Spring Security, JWT и JPA. Используется миграция базы данных через Liquibase.

## Структура проекта

```
src
├── main
│   ├── java/com.example.bankcards
│   │   ├── config           # Конфигурация приложения
│   │   ├── controller       # REST-контроллеры
│   │   ├── dto              # DTO-классы для запросов и ответов
│   │   ├── entity           # JPA-сущности: User, Card, Role и др.
│   │   ├── exception        # Обработка исключений
│   │   ├── repository       # Spring Data JPA репозитории
│   │   ├── security         # Настройка Spring Security и JWT
│   │   ├── service          # Бизнес-логика: AuthService, UserService, CardService, TransferService
│   │   └── util             # Утилиты
│   └── resources
│       ├── db.migration     # Миграции Liquibase (таблицы, начальные данные)
│       └── application.yml  # Конфигурация приложения
├── test                    # Тесты
└── docker-compose.yml      # Docker конфигурация для запуска
```

## Технологии

- Java 17+
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- Liquibase
- PostgreSQL
- Docker & Docker Compose
- Swagger/OpenAPI

## Запуск проекта

### 1. Клонирование проекта

```bash
git clone https://github.com/ADefaultDev/BankCardsProject.git
cd Bank_REST-main
```

### 2. Запуск через Docker

```bash
docker-compose up --build
```

Это поднимет PostgreSQL и запустит backend-приложение.

### 3. Swagger UI

После запуска перейдите по адресу:

```
http://localhost:8080/swagger-ui/index.html
```

По умолчанию есть возможности зайти с ролью `ADMIN` по логину admin, паролю admin123

## Аутентификация и роли

- Поддерживаются два типа пользователей: `ADMIN` и `USER`
- Авторизация через JWT токены
- Доступ к отдельным эндпоинтам ограничен по ролям

## Примеры миграций (Liquibase)

Миграции расположены в `src/main/resources/db/migration`:

- `001-create-tables.yaml` — создание таблиц
- `002-insert-initial-data.yaml` — начальные пользователи, роли и карты
- `db.changelog-master.yaml` — главный changelog Liquibase

## Тестирование

Для запуска тестов:

```bash
./mvnw test
```