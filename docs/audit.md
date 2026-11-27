
# Checkpoint: Pattern Audit

## Project information

- Project: LinguaHub
- Team: Ayan, Aniyar
- Date: 2026-11-27
- Stage: Checkpoint — Pattern Audit
- Baseline: v0.2

## Audit objective

Цель аудита — выявление избыточных архитектурных
решений, недостатков исходного кода и возможностей
упрощения проекта согласно принципу YAGNI
(You Aren't Gonna Need It).

Аудит проведён на основе существующей реализации
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

Исходный код до рефакторинга:

- [CheckoutFacade.java](https://github.com/extwel/LinguaHub/blob/v0.2/src/main/java/kz/kaznaru/capstone/service/CheckoutFacade.java#L36-L37)
- [CartService.java](https://github.com/extwel/LinguaHub/blob/v0.2/src/main/java/kz/kaznaru/capstone/service/CartService.java)
- [Discount.java](https://github.com/extwel/LinguaHub/blob/v0.2/src/main/java/kz/kaznaru/capstone/domain/Discount.java)

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

### Solution

Удалён CheckoutFacade.

Подготовка CheckoutSummary перенесена в CartService.

App использует CartService напрямую.

Сохранена существующая функциональность покупки,
скидок, подарочных сертификатов и уведомлений.

### Decision

Fixed.

Facade удалён согласно принципу YAGNI.

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

Перенос prepareCheckout() в CartService
не устраняет проблему Large Class.

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
| Facade | Дополнительное делегирование одному сервису | Removed |
| Repository | Независимость от реализации хранения данных | Keep |

Repository рассматривается отдельно
от GoF-паттернов.

## Refactoring results

Для текущего Checkpoint выбран недостаток
CS-01 — Middle Man.

Выполненные изменения:

1. Удалён класс CheckoutFacade.
2. Подготовка CheckoutSummary перенесена в CartService.
3. App обновлён для прямой работы с CartService.
4. CheckoutFacadeTest заменён на CartCheckoutTest.
5. EndToEndTest адаптирован.
6. Выполнен mvn clean verify.
7. Изменения оформлены отдельным Pull Request.

### Before refactoring

```java
CheckoutSummary summary =
        checkoutFacade.prepareCheckout();

double paid = checkoutFacade.checkout();
```

### After refactoring

```java
CheckoutSummary summary =
        cartService.prepareCheckout();

double paid = cartService.purchaseCourses();
```

### Pull Request

- PR: #53.
- Branch: refactor/audit-02-remove-facade.
- Status: Merged.
- Merge commit: 36da62746959a79ad3329876f398da51433d05b7.

[View Pull Request](https://github.com/extwel/LinguaHub/pull/53)

### Code changes

| Metric | Result |
|---|---|
| Changed files | 5 |
| Added lines | 45 |
| Deleted lines | 78 |
| Net reduction | 33 |
| Removed production classes | 1 |

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

## Verification

Команда проверки:

```bash
mvn clean verify
```

Результат локальной проверки от 2026-11-27:

```text
Analyzed bundle 'patterns-capstone' with 34 classes
All coverage checks have been met.
BUILD SUCCESS
Total time: 14.956 s
```

Проверяемые сценарии:

- Подготовка заказа.
- Покупка курса.
- Расчёт скидок.
- Применение подарочных сертификатов.
- Очистка корзины после покупки.
- Уведомления Observer.
- Отслеживание прогресса.
- Получение сертификата.

CartCheckoutTest сохраняет проверки
основных сценариев оформления покупки.

EndToEndTest проверяет полный пользовательский сценарий.

## Audit status

- Code smells identification: completed.
- Refactoring: completed.
- Test verification after refactoring: completed.
- Audit ADR: prepared.
- Pattern Register update: prepared.
- README update: prepared.

## Related documents

- [Pattern Register](PATTERN_REGISTER.md)
- [Audit ADR](adr/0004-pattern-audit.md)
- [README](../README.md)
- [Release v0.2](https://github.com/extwel/LinguaHub/releases/tag/v0.2)
- [Refactoring PR #53](https://github.com/extwel/LinguaHub/pull/53)

## Conclusion

В результате аудита выявлены три недостатка
исходного кода LinguaHub.

Middle Man в реализации CheckoutFacade устранён.

Остальные недостатки задокументированы
для возможного дальнейшего улучшения проекта.

Выполненное упрощение уменьшило сложность
архитектуры без потери проверяемой функциональности.

Сборка Maven и проверка покрытия завершены успешно.
