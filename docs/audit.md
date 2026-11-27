
# Checkpoint: Pattern Audit

## Project information

- Project: LinguaHub
- Team: Ayan, Aniyar
- Date: 2026-11-27
- Stage: Checkpoint — Pattern Audit

## Audit objective

Цель аудита — выявление избыточных архитектурных
решений, недостатков исходного кода и возможностей
упрощения проекта согласно принципу YAGNI
(You Aren't Gonna Need It).

Аудит проводится на основе существующей реализации
LinguaHub до рефакторинга.

## Identified code smells

| ID | Code smell | File | Lines | Priority |
|---|---|---|---|---|
| CS-01 | Middle Man | service/CheckoutFacade.java | 36–37 | High |
| CS-02 | Large Class | service/CartService.java | 12, 78, 121, 192 | Medium |
| CS-03 | Repeated Conditional | domain/Discount.java, service/CartService.java | 23, 32; 103 | Medium |

Номера строк соответствуют версии исходного кода
на момент проведения аудита.

Все указанные Java-файлы находятся в каталоге
src/main/java/kz/kaznaru/capstone/.

## CS-01 — Middle Man

### Location

- File: service/CheckoutFacade.java
- Lines: 36–37
- Pattern: Facade

### Evidence

```java
public double checkout() {
    return cartService.purchaseCourses();
}
```

### Problem

CheckoutFacade является дополнительным посредником
между App и CartService.

Метод checkout() не содержит собственной логики
и только перенаправляет вызов в CartService.

Метод prepareCheckout() формирует информацию
о заказе, используя методы того же CartService.

В текущей реализации отсутствуют отдельные
платёжные сервисы или сложные подсистемы,
которые необходимо объединять через Facade.

### Proposed solution

Удалить CheckoutFacade.

Перенести подготовку CheckoutSummary в CartService.

Использовать CartService напрямую из App.

Сохранить существующую функциональность покупки,
скидок, подарочных сертификатов и уведомлений.

### Expected result

- Уменьшение количества классов.
- Удаление лишнего уровня делегирования.
- Упрощение процесса оформления покупки.
- Сохранение бизнес-логики в сервисном слое.

### Decision

Selected for refactoring.

Facade является основным кандидатом на удаление
согласно принципу YAGNI.

## CS-02 — Large Class

### Location

- File: service/CartService.java
- Line 12: объявление класса.
- Line 78: applyDiscount().
- Line 121: applyGiftCertificate().
- Line 192: purchaseCourses().

### Evidence

CartService отвечает за следующие операции:

- Добавление и удаление курсов.
- Управление корзиной.
- Применение скидок.
- Использование подарочных сертификатов.
- Расчёт итоговой стоимости.
- Покупку курсов.
- Хранение приобретённых курсов.
- Уведомление наблюдателей.

### Problem

В одном классе сосредоточено несколько
различных обязанностей.

При дальнейшем расширении проекта это может
усложнить сопровождение, тестирование
и изменение бизнес-логики.

Существует риск нарушения принципа
Single Responsibility Principle.

### Proposed solution

При дальнейшем увеличении функциональности
рассмотреть разделение ответственности между
отдельными сервисами.

Например:

- CartService — управление корзиной.
- GiftCertificateService — управление сертификатами.
- PurchaseService — оформление покупки.

### Decision

Deferred.

На текущем этапе разделение CartService
не выполняется, поскольку это потребует
дополнительных изменений и новых абстракций.

Необходимо избегать преждевременного усложнения
архитектуры согласно YAGNI.

## CS-03 — Repeated Conditional

### Location

- File: domain/Discount.java
- Line 23: проверка процентной скидки.
- Line 32: выбор стратегии через switch.
- File: service/CartService.java
- Line 103: проверка фиксированной скидки.

### Evidence

Discount.java:

```java
if (type == DiscountType.PERCENTAGE && value > 100) {
```

```java
this.strategy = switch (type) {
    case PERCENTAGE -> new PercentageDiscount();
    case FIXED -> new FixedDiscount();
};
```

CartService.java:

```java
if (discount.getType() == DiscountType.FIXED) {
    return Math.min(
            discount.getValue(),
            cart.getTotalPrice()
    );
}
```

### Problem

Несмотря на использование Strategy,
информация о конкретных типах скидок
присутствует в нескольких классах.

CartService содержит отдельное условие
для фиксированной скидки.

При добавлении новых типов скидок
может потребоваться изменение нескольких классов.

При этом выбор стратегии в конструкторе
и проверка допустимого процента сами по себе
являются обоснованными операциями.

Условие в CartService также предотвращает
превышение скидкой стоимости корзины
после удаления курса.

### Proposed solution

В дальнейшем рассмотреть перенос правил
ограничения размера скидки в соответствующие
стратегии или доменный класс Discount.

Необходимо сохранить различие между проверкой
скидки при её применении и пересчётом скидки
после изменения корзины.

### Decision

Deferred.

Strategy сохраняется, поскольку разделяет
алгоритмы расчёта скидок.

Изменение механизма расчёта не входит
в текущий рефакторинг.

## Pattern evaluation

| Pattern | Evaluation | Decision |
|---|---|---|
| Factory Method | Создание различных типов курсов | Keep |
| Strategy | Разделение алгоритмов скидок | Keep |
| Observer | Уведомления и журналирование регистрации | Keep |
| State | Контроль переходов между состояниями | Keep |
| Facade | Дополнительное делегирование одному сервису | Remove |
| Repository | Независимость от реализации хранения данных | Keep |

Repository рассматривается отдельно
от GoF-паттернов.

## Refactoring plan

Для текущего Checkpoint выбран недостаток
CS-01 — Middle Man.

План рефакторинга:

1. Удалить класс CheckoutFacade.
2. Перенести подготовку CheckoutSummary в CartService.
3. Обновить App для прямой работы с CartService.
4. Адаптировать существующие тесты.
5. Сохранить все проверяемые сценарии покупки.
6. Выполнить mvn clean verify.
7. Проверить результаты JUnit, ArchUnit и JaCoCo.
8. Оформить изменения отдельным Pull Request.
9. Создать Audit ADR.
10. Обновить Pattern Register и README.

## YAGNI justification

В текущей версии LinguaHub оформление покупки
выполняется одним сервисом CartService.

Дополнительный Facade не является необходимым
для существующего масштаба приложения.

Facade может снова понадобиться при появлении:

- Отдельного платёжного сервиса.
- Внешних платёжных систем.
- Отдельного сервиса заказов.
- Сервиса регистрации пользователей.
- Сложной координации нескольких подсистем.

До появления этих требований отдельный Facade
считается избыточной абстракцией.

## Verification plan

После рефакторинга необходимо подтвердить:

- Все JUnit-тесты проходят.
- Архитектурные правила ArchUnit соблюдаются.
- Покрытие JaCoCo соответствует порогу проекта.
- Покупка курса работает.
- Скидки рассчитываются корректно.
- Подарочные сертификаты работают.
- Корзина очищается после покупки.
- Observer продолжает отправлять уведомления.
- Прогресс обучения и сертификаты работают.

Команда проверки:

```bash
mvn clean verify
```

## Audit status

- Code smells identification: completed.
- Refactoring: planned.
- Test verification after refactoring: pending.
- Audit ADR: pending.
- Pattern Register update: pending.

## Conclusion

В результате аудита выявлены три недостатка
исходного кода LinguaHub.

Для непосредственного устранения выбран
Middle Man в реализации CheckoutFacade.

Остальные недостатки задокументированы
для возможного дальнейшего улучшения проекта.

Предлагаемое упрощение направлено на уменьшение
сложности архитектуры без потери функциональности.
