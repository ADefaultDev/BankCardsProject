# Используем официальный OpenJDK 21
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем собранный JAR-файл
COPY target/card-system-1.0.0.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Запускаем Spring Boot приложение
ENTRYPOINT ["java", "-jar", "app.jar"]