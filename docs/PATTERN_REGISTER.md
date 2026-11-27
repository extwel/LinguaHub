
# Pattern Register

Проект: LinguaHub

В таблице представлены паттерны проектирования,
реализованные в проекте, их категории, назначение,
основные классы, тесты и рассмотренные альтернативы.

## Implemented patterns

| # | Pattern | Category | Lecture | Problem in this project (the smell it removes) | Key classes | Test | Alternative considered |
|---|---|---|---|---|---|---|---|
| 1 | Factory Method | Creational | practice 1 | Устраняет прямую зависимость приложения от конкретных классов при создании различных типов курсов. | Course, CourseFactory, LanguageCourseFactory, ExamPrepCourseFactory, LanguageCourse, ExamPrepCourse | [CourseFactoryTest](../src/test/java/kz/kaznaru/capstone/CourseFactoryTest.java) | Constructor / direct object creation |
| 2 | Strategy | Behavioral | practice 4 | Разделяет алгоритмы расчёта процентных и фиксированных скидок. | Discount, DiscountStrategy, PercentageDiscount, FixedDiscount | [DiscountStrategyTest](../src/test/java/kz/kaznaru/capstone/DiscountStrategyTest.java) | if/else conditional logic |
| 3 | Observer | Behavioral | practice 4 | Устраняет прямую зависимость процесса регистрации от механизмов уведомления и журналирования. | Enrollment, EnrollmentObserver, EnrollmentNotification, EnrollmentLogger, CartService | [EnrollmentObserverTest](../src/test/java/kz/kaznaru/capstone/EnrollmentObserverTest.java) | Direct method calls |
| 4 | State | Behavioral | practice 5 | Устраняет разрозненные условные операторы при управлении состояниями регистрации и допустимыми переходами. | Enrollment, EnrollmentState, PendingState, ActiveState, CompletedState, CancelledState, CourseProgressService | [EnrollmentStateTest](../src/test/java/kz/kaznaru/capstone/EnrollmentStateTest.java) | Enum + if/else |

## Pattern descriptions

### 1. Factory Method

Категория: Creational.

Используется для создания языковых курсов
и курсов подготовки к экзаменам.

CourseFactory определяет общий метод создания курса.
LanguageCourseFactory и ExamPrepCourseFactory
создают соответствующие объекты.

Преимущество: добавление новых типов курсов
без изменения существующих фабрик.

### 2. Strategy

Категория: Behavioral.

Используется для расчёта скидок.

DiscountStrategy определяет общий интерфейс
алгоритма расчёта скидки.

PercentageDiscount рассчитывает процентную скидку.
FixedDiscount рассчитывает фиксированную скидку.

Преимущество: разделение алгоритмов расчёта
и возможность добавления новых стратегий.

### 3. Observer

Категория: Behavioral.

Используется после успешной покупки курса.

Enrollment уведомляет зарегистрированных наблюдателей
через интерфейс EnrollmentObserver.

EnrollmentNotification формирует уведомление.
EnrollmentLogger сохраняет информацию о регистрации.

Преимущество: добавление новых наблюдателей
без изменения основной логики покупки.

### 4. State

Категория: Behavioral.

Используется для управления состояниями регистрации.

Доступные состояния:

- PendingState — ожидание.
- ActiveState — активное обучение.
- CompletedState — обучение завершено.
- CancelledState — регистрация отменена.

Допустимые переходы:

- PENDING -> ACTIVE.
- PENDING -> CANCELLED.
- ACTIVE -> COMPLETED.

После завершения всех уроков CourseProgressService
переводит регистрацию в состояние COMPLETED.

Преимущество: контроль допустимых переходов
без большого количества условных операторов.

## Architecture pattern

### Repository

Repository используется для отделения бизнес-логики
от конкретного способа хранения данных.

Основные компоненты:

- CourseRepository — интерфейс репозитория.
- InMemoryCourseRepository — хранение курсов в памяти.
- CourseService — бизнес-логика работы с курсами.

Repository рассматривается отдельно от GoF-паттернов.

## Rejected patterns

Следующие паттерны были отклонены при проектировании
или удалены после проведения Pattern Audit
согласно принципу YAGNI.

| Pattern | Why rejected | ADR |
|---|---|---|
| Abstract Factory | Проект не требует создания семейств связанных объектов. Factory Method достаточно для создания существующих типов курсов. | [ADR-0002](adr/0002-pattern-selection.md) |
| Builder | На момент первоначального проектирования отсутствовала необходимость сложного многоступенчатого создания объектов. | [ADR-0002](adr/0002-pattern-selection.md) |
| Mediator | В текущей системе отсутствуют сложные взаимодействия между компонентами, требующие отдельного посредника. | [ADR-0002](adr/0002-pattern-selection.md) |
| Facade | Удалён в AUDIT-02. CheckoutFacade делегировал операции одному CartService и создавал лишний уровень абстракции. | [ADR-0004](adr/0004-pattern-audit.md) |

## Pattern Audit

В версии v0.2 использовалось пять GoF-паттернов.

После проведения Pattern Audit паттерн Facade
удалён согласно принципу YAGNI.

Текущая реализация содержит четыре GoF-паттерна:

- Factory Method.
- Strategy.
- Observer.
- State.

Repository сохранён как архитектурный паттерн.

На текущем этапе отдельный Structural GoF-паттерн
в реализации отсутствует.

Функциональность оформления покупки сохранена
в CartService.

Проверка выполняется с помощью CartCheckoutTest,
PurchaseTest, AppTest и EndToEndTest.

Подробнее:

- [Pattern Audit](audit.md)
- [ADR-0004: Pattern Audit](adr/0004-pattern-audit.md)
- [Pull Request #53](https://github.com/extwel/LinguaHub/pull/53)
