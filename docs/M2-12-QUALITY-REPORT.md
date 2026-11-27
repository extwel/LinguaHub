# M2-12 Quality Check Report

## Итог

Финальная проверка качества проекта LinguaHub завершена успешно. Проверка выполнена на Java 17. Изменения исходного кода и конфигурации Maven не потребовались.

## Окружение

- Java: Eclipse Temurin 17.0.21
- Maven: 3.10.1
- Компиляция: Java 17

## Конфигурация

- JUnit: 5.10.2
- ArchUnit: 1.2.1
- JaCoCo: 0.8.12
- Maven Surefire: 3.2.5
- Минимальный порог JaCoCo для покрытия строк: 60%.

## Результаты тестирования

| Проверка | Результат |
|---|---:|
| Всего тестов | 132 |
| Failures | 0 |
| Errors | 0 |
| Skipped | 0 |
| ArchUnit checks | 3 passed |

## Покрытие JaCoCo

| Метрика | Покрыто | Не покрыто | Покрытие |
|---|---:|---:|---:|
| LINE | 623 | 155 | 80.1% |
| INSTRUCTION | 2359 | 460 | 83.7% |
| BRANCH | 181 | 67 | 73.0% |

Порог LINE в 60% выполнен. Порог не снижался, тесты не удалялись.

## CI

- GitHub Actions использует Eclipse Temurin Java 17.
- Для Pull Request выполняется `mvn -B verify`.
- Отчёт JaCoCo загружается как workflow artifact.

## Совместимость

Предупреждение `Unsupported class file major version 71` возникало при запуске Maven на JDK 27 с JaCoCo 0.8.12. После переключения Maven на JDK 17 команда `mvn clean verify` завершилась успешно; предупреждение больше не появлялось.

## Вывод

M2-12 Quality Check пройден. Изменения production-кода, тестов и конфигурации Maven не потребовались.

Связанный аудит: [Issue #50](https://github.com/extwel/LinguaHub/issues/50).
