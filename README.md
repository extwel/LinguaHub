# LinguaHub

> Capstone project — Application Design Based on Patterns
> (Design Patterns), 4th year, Information Systems.

## Team

- Ayan — extwel
- Aniyar — Aniyar1

## Domain

LinguaHub — онлайн-рынок языковых курсов,
позволяющий пользователям просматривать,
приобретать и проходить языковые курсы.

Пользователи могут применять скидки,
использовать подарочные сертификаты,
отслеживать прогресс обучения и получать
сертификаты после завершения курса.

## Technologies

- Java 17
- Maven
- JUnit 5
- ArchUnit
- JaCoCo
- Git and GitHub
- GitHub Actions
- Mermaid UML

## Quick start

Проверка проекта:

    mvn clean verify

Запуск приложения:

    mvn exec:java -Dexec.mainClass=kz.kaznaru.capstone.App

## Functionality

| ID    | User Story                            |
| ----- | ------------------------------------- |
| US-01 | Просмотр каталога курсов              |
| US-02 | Поиск курсов по языку и уровню        |
| US-03 | Добавление курса в корзину            |
| US-04 | Удаление курса из корзины             |
| US-05 | Применение скидок                     |
| US-06 | Использование подарочных сертификатов |
| US-07 | Подтверждение покупки                 |
| US-08 | Покупка курса и регистрация           |
| US-09 | Отслеживание прогресса обучения       |
| US-10 | Получение сертификата                 |

## Architecture

Проект использует многослойную архитектуру — Layered Architecture.

- domain — классы предметной области и интерфейсы.
- service — бизнес-логика приложения.
- infra — реализация репозиториев и хранение данных.
- ui — компоненты консольного интерфейса.
- App — точка входа и связывание компонентов.

### Architecture diagram

~~~mermaid
flowchart TD
    App["App — Entry Point"]
    UI["UI — Console"]
    Service["Service Layer"]
    Domain["Domain Layer"]
    Infra["Infrastructure Layer"]

    App --> UI
    App --> Service
    App --> Infra
    App --> Domain
    UI --> Service
    Service --> Domain
    Infra --> Domain
~~~

### Class diagram

Диаграммы разделены на три части
для удобства чтения.

#### 1. Course Catalog

Factory Method и Repository.

Новый тип курса добавляется через отдельный
подкласс Course и соответствующую фабрику.

~~~mermaid
classDiagram
    direction TB

    class Course {
        <<abstract>>
        -long id
        -String title
        -String language
        -String level
        -double price
        -int durationHours
        -String description
        -boolean available
        +getId() long
        +getTitle() String
        +getLanguage() String
        +getLevel() String
        +getPrice() double
        +getDurationHours() int
        +getDescription() String
        +isAvailable() boolean
    }

    class LanguageCourse
    class ExamPrepCourse
    class SpeakingCourse

    class CourseFactory {
        <<abstract>>
        +createCourse(...) Course
    }

    class LanguageCourseFactory
    class ExamPrepCourseFactory
    class SpeakingCourseFactory

    class CourseRepository {
        <<interface>>
        +findAll() List~Course~
        +findById(long id) Optional~Course~
        +findAvailableCourses() List~Course~
        +save(Course course) void
    }

    class InMemoryCourseRepository {
        -Map courses
    }

    class CourseService {
        +getAllCourses() List~Course~
        +getAvailableCourses() List~Course~
        +getCourseById(long id) Course
        +findCoursesByLanguageAndLevel(String language, String level) List~Course~
    }

    class CourseCatalogConsole
    class App

    Course <|-- LanguageCourse
    Course <|-- ExamPrepCourse
    Course <|-- SpeakingCourse

    CourseFactory <|-- LanguageCourseFactory
    CourseFactory <|-- ExamPrepCourseFactory
    CourseFactory <|-- SpeakingCourseFactory

    CourseFactory ..> Course
    LanguageCourseFactory ..> LanguageCourse : creates
    ExamPrepCourseFactory ..> ExamPrepCourse : creates
    SpeakingCourseFactory ..> SpeakingCourse : creates

    CourseRepository <|.. InMemoryCourseRepository
    CourseRepository ..> Course
    InMemoryCourseRepository --> Course : stores

    CourseService --> CourseRepository
    CourseCatalogConsole --> CourseService

    App ..> CourseCatalogConsole
    App ..> CourseService
    App ..> InMemoryCourseRepository
    App ..> LanguageCourseFactory
    App ..> ExamPrepCourseFactory
    App ..> SpeakingCourseFactory
~~~

#### 2. Cart and Checkout

Strategy, Observer и Adapter.

CartService использует PaymentProcessor как целевой
интерфейс обработки платежей.

PaymentAdapter преобразует сумму из тенге в тиыны
и передаёт запрос в LegacyPaymentSystem.

LegacyPaymentSystem является учебной симуляцией
платёжной системы и не выполняет реальные
денежные операции.

Adapter также преобразует числовой код результата
платёжной операции в boolean.

При отклонении оплаты покупка не регистрируется,
корзина сохраняется, а Observer не отправляет
уведомления об успешной регистрации.

~~~mermaid
classDiagram
    direction TB

    class App
    class Course

    class Cart {
        -List courses
        +addCourse(Course course) boolean
        +removeCourse(long courseId) boolean
        +getCourses() List~Course~
        +getTotalPrice() double
    }

    class CartService {
        -Cart cart
        -PaymentProcessor paymentProcessor
        +addCourse(Course course) boolean
        +removeCourse(long courseId) boolean
        +applyDiscount(Discount discount) void
        +removeDiscount() void
        +applyGiftCertificate(String code) void
        +getDiscountAmount() double
        +getGiftCertificateAmount() double
        +getFinalPrice() double
        +prepareCheckout() CheckoutSummary
        +purchaseCourses() double
        +getPurchasedCourses() List~Course~
        +isPurchased(long courseId) boolean
    }

    class PaymentProcessor {
        <<interface>>
        +pay(double amount) boolean
    }

    class PaymentAdapter {
        -LegacyPaymentSystem legacyPaymentSystem
        +pay(double amount) boolean
    }

    class LegacyPaymentSystem {
        +processPayment(long amountInTiyn) int
    }

    class Discount {
        -DiscountType type
        -double value
        -DiscountStrategy strategy
        +calculateDiscount(double totalPrice) double
    }

    class DiscountType {
        <<enumeration>>
        PERCENTAGE
        FIXED
    }

    class DiscountStrategy {
        <<interface>>
        +calculateDiscount(double totalPrice, double value) double
    }

    class PercentageDiscount
    class FixedDiscount

    class GiftCertificate {
        -String code
        -double amount
        -boolean used
        +use() void
        +isUsed() boolean
    }

    class CheckoutSummary {
        -double totalPrice
        -double discountAmount
        -double certificateAmount
        -double finalPrice
    }

    class Enrollment {
        -List observers
        +addObserver(EnrollmentObserver observer) void
        +removeObserver(EnrollmentObserver observer) void
        +notifyObservers(Course course) void
    }

    class EnrollmentObserver {
        <<interface>>
        +update(Course course) void
    }

    class EnrollmentNotification
    class EnrollmentLogger

    App --> CartService
    App ..> CheckoutSummary

    CartService ..> CheckoutSummary : creates
    CartService --> Cart
    CartService --> PaymentProcessor : uses
    CartService ..> PaymentAdapter : default implementation

    PaymentProcessor <|.. PaymentAdapter
    PaymentAdapter --> LegacyPaymentSystem : adapts

    CartService --> Discount
    CartService --> GiftCertificate
    CartService --> Enrollment
    CartService --> EnrollmentNotification
    CartService --> EnrollmentLogger
    CartService --> Course : purchased courses

    Cart --> Course : contains

    Discount --> DiscountType
    Discount --> DiscountStrategy

    DiscountStrategy <|.. PercentageDiscount
    DiscountStrategy <|.. FixedDiscount

    Enrollment --> EnrollmentObserver : notifies
    EnrollmentObserver <|.. EnrollmentNotification
    EnrollmentObserver <|.. EnrollmentLogger
~~~

#### 3. Course Progress and Certificates

Iterator, Proxy и State — управление доступом
к обучению, последовательным обходом уроков
и жизненным циклом регистрации.

CourseProgress реализует стандартный интерфейс
Java Iterable, а LessonIterator реализует Iterator.

CourseAccessProxy контролирует доступ к приобретённым
курсам и делегирует разрешённые операции
CourseProgressService.

Enrollment управляет текущим состоянием регистрации
и определяет возможность продолжения обучения.

~~~mermaid
classDiagram
    direction TB

    class App
    class Course
    class CartService

    class CourseAccess {
        <<interface>>
        +openCourse(Course course) CourseProgress
        +completeLesson(long courseId, long lessonId) boolean
    }

    class CourseAccessProxy {
        -CartService cartService
        -CourseAccess realAccess
        -checkAccess(long courseId) void
        +openCourse(Course course) CourseProgress
        +completeLesson(long courseId, long lessonId) boolean
    }

    class CourseProgressService {
        -Map progressMap
        -Map enrollments
        +openCourse(Course course) CourseProgress
        +getProgress(long courseId) CourseProgress
        +getEnrollmentStatus(long courseId) String
        +completeLesson(long courseId, long lessonId) boolean
    }

    class CourseProgress {
        -long courseId
        -List lessons
        -List completedLessonIds
        +getCourseId() long
        +getLessons() List~Lesson~
        +iterator() Iterator~Lesson~
        +completeLesson(long lessonId) boolean
        +getCompletedLessonIds() List~Long~
        +getCompletedLessons() List~Lesson~
        +getRemainingLessons() List~Lesson~
        +getCompletedCount() int
        +getRemainingCount() int
        +getCompletionPercentage() double
    }

    class Lesson {
        -long id
        -String title
        +getId() long
        +getTitle() String
    }

    class LessonIterator {
        -List lessons
        -int position
        +hasNext() boolean
        +next() Lesson
    }

    class Iterable~Lesson~ {
        <<interface>>
        +iterator() Iterator~Lesson~
    }

    class Iterator~Lesson~ {
        <<interface>>
        +hasNext() boolean
        +next() Lesson
    }

    class Enrollment {
        -EnrollmentState state
        +getState() EnrollmentState
        +getStatus() String
        +canContinueLearning() boolean
        +activate() void
        +complete() void
        +cancel() void
    }

    class EnrollmentState {
        <<interface>>
        +getName() String
        +canContinueLearning() boolean
        +activate(Enrollment enrollment) void
        +complete(Enrollment enrollment) void
        +cancel(Enrollment enrollment) void
    }

    class PendingState
    class ActiveState
    class CompletedState
    class CancelledState

    class CertificateService {
        -CartService cartService
        -CourseProgressService progressService
        +issueCertificate(long courseId, String studentName) CompletionCertificate
        +hasCertificate(long courseId) boolean
        +getCertificate(long courseId) CompletionCertificate
    }

    class CompletionCertificate {
        -String studentName
        -String courseName
        -LocalDate issueDate
        +getStudentName() String
        +getCourseName() String
        +getIssueDate() LocalDate
    }

    App --> CourseAccess : uses
    App ..> CourseAccessProxy : creates
    App ..> CourseProgressService : creates
    App --> CertificateService

    CourseAccess <|.. CourseAccessProxy
    CourseAccess <|.. CourseProgressService

    CourseAccessProxy --> CartService : checks purchase
    CourseAccessProxy --> CourseAccess : delegates

    CourseProgressService --> CourseProgress
    CourseProgressService --> Enrollment
    CourseProgressService ..> Course

    CourseProgress --> Lesson
    CourseProgress ..> LessonIterator : creates

    Iterable~Lesson~ <|.. CourseProgress
    Iterator~Lesson~ <|.. LessonIterator

    Enrollment --> EnrollmentState

    EnrollmentState <|.. PendingState
    EnrollmentState <|.. ActiveState
    EnrollmentState <|.. CompletedState
    EnrollmentState <|.. CancelledState

    CertificateService --> CartService
    CertificateService --> CourseProgressService
    CertificateService --> CompletionCertificate
~~~

### State diagram

~~~mermaid
stateDiagram-v2
    [*] --> PENDING

    PENDING --> ACTIVE : activate
    PENDING --> CANCELLED : cancel

    ACTIVE --> COMPLETED : complete

    COMPLETED --> [*]
    CANCELLED --> [*]
~~~

Регистрация создаётся в состоянии PENDING.

При первом открытии курса CourseProgressService
активирует регистрацию, переводя её в ACTIVE.

Состояние ACTIVE позволяет завершать уроки.

После завершения всех уроков состояние изменяется
на COMPLETED.

В состоянии COMPLETED дальнейшее выполнение
операций завершения уроков запрещено.

Просмотр завершённого курса и получение
сертификата остаются доступными.

CANCELLED является терминальным состоянием.
В текущем консольном интерфейсе отдельная
операция отмены регистрации не предусмотрена.

### Proxy and State collaboration

Proxy и State решают разные, дополняющие
друг друга задачи:

- **Proxy (CourseAccessProxy)** проверяет,
  приобретён ли курс пользователем.

- Если курс не куплен, операция отклоняется
  и запрос не передаётся реальному сервису.

- **State (Enrollment)** управляет состоянием
  регистрации и определяет возможность
  продолжения обучения.

- После успешной проверки Proxy делегирует
  запрос CourseProgressService.

- CourseProgressService проверяет
  Enrollment.canContinueLearning()
  перед завершением урока.

- После завершения последнего урока сервис
  переводит регистрацию в COMPLETED.

- Последующие попытки завершить урок
  блокируются проверкой состояния.

Таким образом, Proxy отвечает за проверку
покупки и доступ к курсу, а State — за правила
обучения на разных этапах регистрации.

### Combination diagram

~~~mermaid
flowchart TD
    App["App"]
    Proxy["CourseAccessProxy"]
    Purchase{"Курс приобретён?"}
    Service["CourseProgressService"]
    Enrollment["Enrollment"]
    State{"canContinueLearning()"}
    Complete["Завершить урок"]
    Last{"Последний урок?"}
    Finished["COMPLETED"]
    Active["ACTIVE"]
    Denied["Доступ запрещён"]
    Blocked["Операция запрещена"]

    App --> Proxy
    Proxy --> Purchase

    Purchase -->|Нет| Denied
    Purchase -->|Да| Service

    Service --> Enrollment
    Enrollment --> State

    State -->|Нет| Blocked
    State -->|Да| Complete

    Complete --> Last
    Last -->|Да| Finished
    Last -->|Нет| Active
~~~

Диаграмма описывает совместную работу Proxy
и State при операции завершения урока.

Реализация:
[M3-04 — Combination PR #68](https://github.com/extwel/LinguaHub/pull/68).

Отдельный тест комбинации:
`ProxyStateCombinationTest`.

Тест проверяет пять сценариев:

- Запрет доступа к неприобретённому курсу.
- Открытие приобретённого курса в состоянии ACTIVE.
- Завершение урока в состоянии ACTIVE.
- Переход в COMPLETED после последнего урока.
- Запрет дальнейшего завершения уроков после COMPLETED.

## Implemented patterns

| Pattern        | Category   | Purpose                                       | Test                   |
| -------------- | ---------- | --------------------------------------------- | ---------------------- |
| Factory Method | Creational | Создание различных типов курсов через фабрики | CourseFactoryTest      |
| Strategy       | Behavioral | Различные алгоритмы скидок                    | DiscountStrategyTest   |
| Observer       | Behavioral | Уведомления и журналирование после покупки    | EnrollmentObserverTest |
| State          | Behavioral | Управление состояниями регистрации            | EnrollmentStateTest    |
| Adapter        | Structural | Адаптация интерфейса платёжной системы        | PaymentAdapterTest     |
| Iterator       | Behavioral | Последовательный обход уроков курса           | LessonIteratorTest     |
| Proxy          | Structural | Контроль доступа к приобретённым курсам       | CourseAccessProxyTest  |

Всего реализовано семь GoF-паттернов.

| Category   | Count |
| ---------- | ----: |
| Creational |     1 |
| Structural |     2 |
| Behavioral |     4 |
| Total      |     7 |

### Pattern Combination

| Combination   | Purpose                                                      | Test                      |
| ------------- | ------------------------------------------------------------ | ------------------------- |
| Proxy + State | Проверка приобретения курса и допустимости операций обучения | ProxyStateCombinationTest |

Комбинация не считается отдельным восьмым
GoF-паттерном.

### Extension Point — Factory Method

В рамках M3-05 продемонстрирована расширяемость
архитектуры через существующий Factory Method.

В проект добавлен новый тип курса SpeakingCourse
и фабрика SpeakingCourseFactory.

SpeakingCourseFactory наследуется от CourseFactory
и переопределяет метод createCourse(),
создавая объект SpeakingCourse.

SpeakingCourse наследуется от Course.

Новый тип регистрируется в существующем каталоге
и использует уже реализованные компоненты:

- CourseRepository.
- CourseService.
- CartService.
- DiscountStrategy.
- PaymentAdapter.
- CourseAccessProxy.
- LessonIterator.
- EnrollmentState.
- CertificateService.

Новый курс:

| Property  | Value               |
| --------- | ------------------- |
| ID        | 5                   |
| Title     | English Speaking B1 |
| Language  | English             |
| Level     | Intermediate        |
| Price     | 35000 KZT           |
| Duration  | 50 hours            |
| Available | true                |

#### Extension Point diagram

~~~mermaid
classDiagram
    direction TB

    class Course {
        <<abstract>>
    }

    class CourseFactory {
        <<abstract>>
        +createCourse(...) Course
    }

    class LanguageCourse
    class ExamPrepCourse
    class SpeakingCourse

    class LanguageCourseFactory
    class ExamPrepCourseFactory
    class SpeakingCourseFactory

    Course <|-- LanguageCourse
    Course <|-- ExamPrepCourse
    Course <|-- SpeakingCourse

    CourseFactory <|-- LanguageCourseFactory
    CourseFactory <|-- ExamPrepCourseFactory
    CourseFactory <|-- SpeakingCourseFactory

    LanguageCourseFactory ..> LanguageCourse : creates
    ExamPrepCourseFactory ..> ExamPrepCourse : creates
    SpeakingCourseFactory ..> SpeakingCourse : creates
~~~

#### Evidence of Extension Point

Реализация выполнена в отдельном Pull Request.

[M3-05 — Extension Point PR #69](https://github.com/extwel/LinguaHub/pull/69).

| File                       | Status   | Added lines | Deleted lines |
| -------------------------- | -------- | ----------: | ------------: |
| App.java                   | Modified |           1 |             0 |
| SpeakingCourse.java        | New      |          27 |             0 |
| SpeakingCourseFactory.java | New      |          28 |             0 |
| ExtensionPointTest.java    | New      |         321 |             0 |
| Total                      | 4 files  |         377 |             0 |

В существующем App.java добавлена ровно одна строка:

~~~java
repository.save(new kz.kaznaru.capstone.domain.SpeakingCourseFactory().createCourse(5, "English Speaking B1", "English", "Intermediate", 35000, 50, "Курс разговорного английского языка.", true));
~~~

Другие существующие файлы не изменены.

Для добавления нового типа не потребовалось
изменять базовый класс Course, абстрактную
фабрику CourseFactory, существующие фабрики,
репозиторий или сервисы.

ExtensionPointTest содержит десять тестов,
проверяющих создание, хранение, поиск,
покупку и прохождение нового курса.

Результаты на момент выполнения M3-05:

- ExtensionPointTest: 10/10.
- Всего тестов: 186.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- ArchitectureTest: 3/3.
- Все пороги JaCoCo выполнены.
- Maven clean verify: BUILD SUCCESS.

Новый тип курса использует общий механизм
формирования уроков. Отдельная программа
разговорных занятий не реализована.

Это демонстрирует принцип Open/Closed Principle:
существующая архитектура позволяет добавлять
новые типы курсов без изменения бизнес-логики.

### Architecture pattern

Repository — отделение бизнес-логики
от конкретного способа хранения данных.

CourseService использует интерфейс
CourseRepository.

InMemoryCourseRepository реализует этот
интерфейс и хранит данные в памяти.

Repository учитывается отдельно
от семи реализованных GoF-паттернов.

### Rejected patterns

- Abstract Factory — отсутствует необходимость
  создавать семейства связанных объектов.

- Builder — на момент проектирования отсутствовала
  необходимость сложного создания объектов.

- Mediator — отсутствует необходимость
  в отдельном посреднике между компонентами.

- Facade — удалён после Pattern Audit,
  поскольку создавал лишний уровень делегирования.

Подробнее:

- [Pattern Register](docs/PATTERN_REGISTER.md)
- [ADR-0001: Technology Stack](docs/adr/0001-technology-stack.md)
- [ADR-0002: Pattern Selection](docs/adr/0002-pattern-selection.md)
- [ADR-0003: Layered Architecture](docs/adr/0003-template.md)
- [ADR-0004: Pattern Audit](docs/adr/0004-pattern-audit.md)
- [ADR-0005: Advanced Patterns and Extensibility](docs/adr/0005-advanced-patterns.md)

## M2-02 / Main Scenario — Purchase and Learning Lifecycle

Основной сценарий демонстрирует полный
жизненный цикл курса:

1. Просмотр каталога.
2. Выбор курса.
3. Добавление курса в корзину.
4. Применение скидок и подарочного сертификата.
5. Подтверждение покупки.
6. Оплата через Adapter.
7. Регистрация покупки и уведомления Observer.
8. Открытие курса через Proxy.
9. Отображение уроков через Iterator.
10. Завершение уроков и переходы State.
11. Получение сертификата.

CertificateService отдельно проверяет покупку
и завершённость курса.

В текущей реализации CertificateService
использует CartService и CourseProgressService
напрямую, а не через CourseAccessProxy.

### Sequence Diagram

~~~mermaid
sequenceDiagram
    actor User
    participant App
    participant CartService
    participant Cart
    participant Adapter as PaymentAdapter
    participant Legacy as LegacyPaymentSystem
    participant ObserverEnrollment as Enrollment (Observer)
    participant Notification as EnrollmentNotification
    participant Logger as EnrollmentLogger
    participant Proxy as CourseAccessProxy
    participant ProgressService as CourseProgressService
    participant Progress as CourseProgress
    participant LearningEnrollment as Enrollment (State)
    participant Iterator as LessonIterator
    participant CertificateService

    User->>App: Выбрать курс и добавить в корзину
    App->>CartService: addCourse(course)
    CartService->>Cart: addCourse(course)
    Cart-->>CartService: true
    CartService-->>App: Курс добавлен

    opt Применение скидки
        User->>App: Выбрать скидку
        App->>CartService: applyDiscount(discount)
    end

    opt Применение подарочного сертификата
        User->>App: Ввести код сертификата
        App->>CartService: applyGiftCertificate(code)
    end

    User->>App: Оформить покупку
    App->>CartService: prepareCheckout()
    CartService->>Cart: getTotalPrice()
    Cart-->>CartService: Сумма заказа
    CartService-->>App: CheckoutSummary
    App-->>User: Показать итоговую стоимость

    User->>App: Подтвердить оплату
    App->>CartService: purchaseCourses()

    opt Итоговая стоимость больше нуля
        CartService->>Adapter: pay(finalPrice)
        Adapter->>Adapter: Преобразовать KZT в тиыны
        Adapter->>Legacy: processPayment(amountInTiyn)
        Legacy-->>Adapter: SUCCESS
        Adapter-->>CartService: true
    end

    Note over CartService,Legacy: При отказе оплаты покупка не регистрируется

    CartService->>CartService: Сохранить приобретённые курсы

    loop Для каждого приобретённого курса
        CartService->>Cart: removeCourse(courseId)
    end

    CartService->>CartService: Сбросить скидку и применённый сертификат

    loop Для каждого приобретённого курса
        CartService->>ObserverEnrollment: notifyObservers(course)
        ObserverEnrollment->>Notification: update(course)
        ObserverEnrollment->>Logger: update(course)
    end

    CartService-->>App: Итоговая стоимость
    App-->>User: Покупка успешна

    User->>App: Открыть приобретённый курс
    App->>Proxy: openCourse(course)
    Proxy->>CartService: isPurchased(courseId)
    CartService-->>Proxy: true
    Proxy->>CartService: getPurchasedCourses()
    CartService-->>Proxy: Приобретённые курсы
    Proxy->>ProgressService: openCourse(purchasedCourse)

    opt Первое открытие курса
        ProgressService->>ProgressService: Создать CourseProgress и Enrollment
        ProgressService->>LearningEnrollment: activate()
        LearningEnrollment->>LearningEnrollment: PENDING → ACTIVE
    end

    ProgressService-->>Proxy: CourseProgress
    Proxy-->>App: CourseProgress

    loop Пока пользователь находится в меню обучения

        App->>Progress: iterator()
        Progress->>Iterator: new LessonIterator(lessons)

        loop Пока Iterator.hasNext()
            App->>Iterator: hasNext()
            Iterator-->>App: true
            App->>Iterator: next()
            Iterator-->>App: Lesson
            App-->>User: Показать урок и его статус
        end

        alt Пользователь выбирает завершение урока

            User->>App: Ввести ID урока
            App->>Proxy: completeLesson(courseId, lessonId)
            Proxy->>CartService: isPurchased(courseId)
            CartService-->>Proxy: true
            Proxy->>ProgressService: completeLesson(courseId, lessonId)

            ProgressService->>LearningEnrollment: canContinueLearning()

            alt Состояние ACTIVE
                LearningEnrollment-->>ProgressService: true

                ProgressService->>Progress: completeLesson(lessonId)
                Progress-->>ProgressService: Результат завершения

                opt Завершён последний урок
                    ProgressService->>LearningEnrollment: complete()
                    LearningEnrollment->>LearningEnrollment: ACTIVE → COMPLETED
                end

                ProgressService-->>Proxy: Результат
                Proxy-->>App: Результат
                App-->>User: Результат завершения урока

            else Обучение недоступно
                LearningEnrollment-->>ProgressService: false
                ProgressService-->>Proxy: IllegalStateException
                Proxy-->>App: IllegalStateException
                Note over App: Обработка исключения требует исправления
            end

        else Возвращение в главное меню
            User->>App: Выбрать выход из меню курса
        end
    end

    User->>App: Запросить сертификат
    App->>CertificateService: issueCertificate(courseId, studentName)

    CertificateService->>CartService: getPurchasedCourses()
    CartService-->>CertificateService: Приобретённые курсы

    CertificateService->>ProgressService: openCourse(course)
    ProgressService-->>CertificateService: CourseProgress

    CertificateService->>Progress: getRemainingLessons()
    Progress-->>CertificateService: Пустой список

    CertificateService->>CertificateService: Получить или создать CompletionCertificate
    CertificateService-->>App: CompletionCertificate
    App-->>User: Выдать сертификат
~~~

Диаграмма демонстрирует основной успешный
сценарий с дополнительными ветками проверки
состояния регистрации.

Объект Enrollment, используемый CartService
для уведомления Observer, является отдельным
экземпляром от объектов Enrollment, используемых
CourseProgressService для управления состоянием
регистрации каждого курса.

### Responsibilities

- App — взаимодействие с пользователем
  и координация сценария.

- CourseFactory — создание объектов курсов.

- CartService — корзина, скидки,
  подготовка заказа, оплата и список покупок.

- PaymentProcessor — целевой интерфейс
  платёжной операции.

- PaymentAdapter — преобразование суммы
  и вызов LegacyPaymentSystem.

- LegacyPaymentSystem — учебная симуляция
  обработки платежа в тиынах.

- CheckoutSummary — информация
  о стоимости заказа.

- Cart — хранение выбранных курсов.

- DiscountStrategy — алгоритмы расчёта скидок.

- Enrollment — управление регистрацией
  и уведомление наблюдателей.

- CourseAccess — общий интерфейс
  доступа к обучению.

- CourseAccessProxy — проверка приобретения
  курса перед доступом к обучению.

- CourseProgressService — управление
  прогрессом и состоянием регистрации.

- CourseProgress — хранение уроков
  и выполненного прогресса.

- LessonIterator — последовательный
  обход уроков.

- EnrollmentState — определение допустимых
  операций в текущем состоянии регистрации.

- CertificateService — проверка завершения
  курса и выдача сертификатов.

- CourseRepository — доступ к каталогу курсов.

## Pattern Audit

В рамках Checkpoint: Pattern Audit
обнаружены три code smells.

| Code smell           | Decision |
| -------------------- | -------- |
| Middle Man           | Fixed    |
| Large Class          | Deferred |
| Repeated Conditional | Deferred |

Паттерн Facade удалён согласно принципу YAGNI.

App взаимодействует напрямую с CartService.

Рефакторинг выполнен отдельным Pull Request #53.

Результаты рефакторинга:

- Удалён один класс.
- Изменено пять файлов.
- Код и тесты сокращены на 33 строки.
- Maven clean verify: BUILD SUCCESS.
- Все пороги JaCoCo выполнены.

Документация:

- [Pattern Audit](docs/audit.md)
- [Audit ADR](docs/adr/0004-pattern-audit.md)
- [Refactoring PR #53](https://github.com/extwel/LinguaHub/pull/53)

## Testing

Для проверки проекта используются:

- JUnit 5 — функциональное тестирование.
- ArchUnit — проверка архитектурных зависимостей.
- JaCoCo — анализ покрытия кода тестами.

Запуск всех тестов:

    mvn clean test

Полная проверка проекта:

    mvn clean verify

### Pattern tests

| Pattern                    | Test class                |
| -------------------------- | ------------------------- |
| Factory Method             | CourseFactoryTest         |
| Strategy                   | DiscountStrategyTest      |
| Observer                   | EnrollmentObserverTest    |
| State                      | EnrollmentStateTest       |
| Adapter                    | PaymentAdapterTest        |
| Iterator                   | LessonIteratorTest        |
| Proxy                      | CourseAccessProxyTest     |
| Combination: Proxy + State | ProxyStateCombinationTest |
| Extension Point            | ExtensionPointTest        |

### Functional tests

Основной сценарий покупки проверяется
в следующих тестах:

- PurchaseTest.
- AppTest.
- CartCheckoutTest.

EndToEndTest проверяет полный сценарий
от поиска курса до получения сертификата.

### Architecture tests

ArchitectureTest проверяет соблюдение
архитектурных зависимостей между слоями.

Проверяются следующие ограничения:

- Domain не зависит от service, infra и ui.
- Service не зависит от infra и ui.
- UI не зависит от infra.

### Coverage

JaCoCo используется для анализа
покрытия исходного кода.

Минимальный порог покрытия строк — 60%.

### Test results

Результаты на момент завершения M3-05:

| Metric                    | Result |
| ------------------------- | -----: |
| Tests                     |    186 |
| Failures                  |      0 |
| Errors                    |      0 |
| Skipped                   |      0 |
| Architecture tests        |    3/3 |
| CourseFactoryTest         |    6/6 |
| DiscountStrategyTest      |    6/6 |
| EnrollmentObserverTest    |    7/7 |
| EnrollmentStateTest       |  10/10 |
| PaymentAdapterTest        |  18/18 |
| LessonIteratorTest        |    4/4 |
| CourseAccessProxyTest     |  17/17 |
| ProxyStateCombinationTest |    5/5 |
| ExtensionPointTest        |  10/10 |
| Analyzed classes          |     40 |

- Java release: 17.
- JaCoCo coverage checks: passed.
- Maven clean verify: BUILD SUCCESS.
- Время сборки: 15.495 секунды.

Финальные результаты этапа M3 будут
зафиксированы после итоговой проверки качества.

## Known limitations

- Платёжная система является учебной симуляцией.
- Данные хранятся в оперативной памяти.
- Proxy контролирует операции, выполняемые
  через интерфейс CourseAccess, и не является
  полноценной системой аутентификации.
- Новый SpeakingCourse использует общий
  механизм формирования уроков.
- Имя студента при выдаче сертификата
  в консольном приложении задано заранее.
- Повторная попытка завершить урок после
  перехода в COMPLETED вызывает IllegalStateException.
  Обработка этого исключения в App требует
  отдельного исправления.

## Releases

- [v0.2 — M2](https://github.com/extwel/LinguaHub/releases/tag/v0.2)

Версия v0.2 фиксирует состояние проекта
до рефакторинга Pattern Audit.

Релиз v0.3 будет опубликован после завершения
документации и финальных проверок этапа M3.

## M3 — Advanced Patterns

В рамках этапа M3 реализованы:

| Task  | Description                | Pull Request                                          |
| ----- | -------------------------- | ----------------------------------------------------- |
| M3-01 | Adapter                    | [PR #65](https://github.com/extwel/LinguaHub/pull/65) |
| M3-02 | Iterator                   | [PR #66](https://github.com/extwel/LinguaHub/pull/66) |
| M3-03 | Proxy                      | [PR #67](https://github.com/extwel/LinguaHub/pull/67) |
| M3-04 | Combination: Proxy + State | [PR #68](https://github.com/extwel/LinguaHub/pull/68) |
| M3-05 | Extension Point            | [PR #69](https://github.com/extwel/LinguaHub/pull/69) |

Результат реализации:

- Семь GoF-паттернов.
- Все три категории паттернов.
- Отдельный тест каждого нового паттерна.
- Комбинация Proxy и State с отдельным тестом.
- Extension Point через Factory Method.
- Обновлённые архитектурные диаграммы.
- Успешная локальная сборка Maven.
- Сохранение существующей функциональности.

## Team workflow

- One issue per task.
- One feature branch per issue.
- Pull request per branch.
- Automated tests before merge.
- One teammate review before merge.
- No direct commits to main.
