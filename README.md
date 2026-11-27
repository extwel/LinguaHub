
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

- Java
- Maven
- JUnit 5
- ArchUnit
- JaCoCo
- Git and GitHub
- Mermaid UML

## Quick start

Проверка проекта:

```bash
mvn clean verify
```

Запуск приложения:

```bash
mvn exec:java -Dexec.mainClass=kz.kaznaru.capstone.App
```

## Functionality

| ID | User Story |
|---|---|
| US-01 | Просмотр каталога курсов |
| US-02 | Поиск курсов по языку и уровню |
| US-03 | Добавление курса в корзину |
| US-04 | Удаление курса из корзины |
| US-05 | Применение скидок |
| US-06 | Использование подарочных сертификатов |
| US-07 | Подтверждение покупки |
| US-08 | Покупка курса и регистрация |
| US-09 | Отслеживание прогресса обучения |
| US-10 | Получение сертификата |

## Architecture

Проект использует многослойную архитектуру.

- domain — классы предметной области и интерфейсы.
- service — бизнес-логика приложения.
- infra — реализация репозиториев и хранение данных.
- ui — компоненты консольного интерфейса.
- App — точка входа и связывание компонентов.

### Architecture diagram

```mermaid
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
```

### Class diagram

Диаграммы разделены на три части
для удобства чтения.

#### 1. Course Catalog

Factory Method и Repository.

```mermaid
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
        +isAvailable() boolean
    }

    class LanguageCourse
    class ExamPrepCourse

    class CourseFactory {
        <<abstract>>
        +createCourse(...) Course
    }

    class LanguageCourseFactory
    class ExamPrepCourseFactory

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

    CourseFactory <|-- LanguageCourseFactory
    CourseFactory <|-- ExamPrepCourseFactory

    CourseFactory ..> Course
    LanguageCourseFactory ..> LanguageCourse : creates
    ExamPrepCourseFactory ..> ExamPrepCourse : creates

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
```

#### 2. Cart and Checkout

Strategy, Observer и Facade.

```mermaid
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
        +addCourse(Course course) boolean
        +removeCourse(long courseId) boolean
        +applyDiscount(Discount discount) void
        +removeDiscount() void
        +applyGiftCertificate(String code) void
        +getDiscountAmount() double
        +getGiftCertificateAmount() double
        +getFinalPrice() double
        +purchaseCourses() double
        +getPurchasedCourses() List~Course~
        +isPurchased(long courseId) boolean
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

    class CheckoutFacade {
        +prepareCheckout() CheckoutSummary
        +checkout() double
    }

    class CheckoutSummary {
        -double totalPrice
        -double discountAmount
        -double certificateAmount
        -double finalPrice
    }

    class Enrollment {
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
    App --> CheckoutFacade

    CheckoutFacade --> CartService
    CheckoutFacade ..> CheckoutSummary

    CartService --> Cart
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
```

#### 3. Course Progress and Certificates

State и управление прогрессом обучения.

```mermaid
classDiagram
    direction TB

    class App
    class Course
    class CartService

    class Lesson {
        -long id
        -String title
        +getId() long
        +getTitle() String
    }

    class CourseProgress {
        -long courseId
        -List lessons
        -List completedLessonIds
        +completeLesson(long lessonId) boolean
        +getCompletedCount() int
        +getRemainingCount() int
        +getCompletionPercentage() double
    }

    class CourseProgressService {
        +openCourse(Course course) CourseProgress
        +getProgress(long courseId) CourseProgress
        +completeLesson(long courseId, long lessonId) boolean
        +getEnrollmentStatus(long courseId) String
    }

    class Enrollment {
        -EnrollmentState state
        +getStatus() String
        +activate() void
        +complete() void
        +cancel() void
    }

    class EnrollmentState {
        <<interface>>
        +getName() String
        +activate(Enrollment enrollment) void
        +complete(Enrollment enrollment) void
        +cancel(Enrollment enrollment) void
    }

    class PendingState
    class ActiveState
    class CompletedState
    class CancelledState

    class CompletionCertificate {
        -String studentName
        -String courseName
        -LocalDate issueDate
        +getStudentName() String
        +getCourseName() String
        +getIssueDate() LocalDate
    }

    class CertificateService {
        +issueCertificate(long courseId, String studentName) CompletionCertificate
        +hasCertificate(long courseId) boolean
        +getCertificate(long courseId) CompletionCertificate
    }

    App --> CourseProgressService
    App --> CertificateService

    CourseProgressService --> CourseProgress
    CourseProgressService --> Enrollment
    CourseProgressService ..> Course

    CourseProgress --> Lesson

    Enrollment --> EnrollmentState

    EnrollmentState <|.. PendingState
    EnrollmentState <|.. ActiveState
    EnrollmentState <|.. CompletedState
    EnrollmentState <|.. CancelledState

    CertificateService --> CartService
    CertificateService --> CourseProgressService
    CertificateService --> CompletionCertificate
```

### State diagram

```mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> ACTIVE : activate
    PENDING --> CANCELLED : cancel
    ACTIVE --> COMPLETED : complete
    COMPLETED --> [*]
    CANCELLED --> [*]
```

Регистрация для отслеживания прогресса
переходит в ACTIVE при первом открытии курса.

После завершения всех уроков состояние
изменяется на COMPLETED.

## Implemented patterns

| Pattern | Category | Purpose | Test |
|---|---|---|---|
| Factory Method | Creational | Создание различных типов курсов | CourseFactoryTest |
| Strategy | Behavioral | Различные алгоритмы скидок | DiscountStrategyTest |
| Observer | Behavioral | Уведомления и журналирование регистрации | EnrollmentObserverTest |
| State | Behavioral | Управление состояниями регистрации | EnrollmentStateTest |
| Facade | Structural | Упрощение оформления покупки | CheckoutFacadeTest |

### Architecture pattern

Repository — отделение бизнес-логики
от конкретного способа хранения данных.

CourseService использует интерфейс CourseRepository.

InMemoryCourseRepository реализует этот интерфейс
и хранит данные в памяти.

### Rejected patterns

- Abstract Factory — отсутствует необходимость
  создавать семейства связанных объектов.
- Builder — объекты курсов имеют простую структуру.
- Mediator — отсутствует необходимость
  в отдельном посреднике между компонентами.

Подробнее:

- [Pattern Register](docs/PATTERN_REGISTER.md)
- [ADR-0002: Pattern Selection](docs/adr/0002-pattern-selection.md)
- [ADR-0003: Layered Architecture](docs/adr/0003-template.md)

## M2-02 — Service Layer

Основной сценарий приложения LinguaHub —
покупка курсов пользователем.

Бизнес-логика покупки реализована в CartService.

CheckoutFacade предоставляет упрощённый интерфейс
для подготовки и оформления покупки.

### Sequence Diagram

```mermaid
sequenceDiagram
    actor User
    participant App
    participant CheckoutFacade
    participant CartService
    participant Cart
    participant Enrollment
    participant Notification as EnrollmentNotification
    participant Logger as EnrollmentLogger

    User->>App: Оформить покупку
    App->>CheckoutFacade: prepareCheckout()

    CheckoutFacade->>CartService: getCourses()
    CartService->>Cart: getCourses()
    Cart-->>CartService: Список курсов
    CartService-->>CheckoutFacade: Список курсов

    CheckoutFacade->>CartService: getTotalPrice()
    CartService-->>CheckoutFacade: Стоимость

    CheckoutFacade->>CartService: getDiscountAmount()
    CartService-->>CheckoutFacade: Размер скидки

    CheckoutFacade->>CartService: getGiftCertificateAmount()
    CartService-->>CheckoutFacade: Сумма сертификата

    CheckoutFacade->>CartService: getFinalPrice()
    CartService-->>CheckoutFacade: Итоговая стоимость

    CheckoutFacade-->>App: CheckoutSummary
    App-->>User: Информация о заказе

    alt Пользователь подтверждает покупку
        User->>App: Подтвердить
        App->>CheckoutFacade: checkout()
        CheckoutFacade->>CartService: purchaseCourses()

        CartService->>Cart: getCourses()
        Cart-->>CartService: Список курсов

        CartService->>CartService: Проверка заказа

        alt Заказ корректен
            CartService->>CartService: getFinalPrice()
            CartService->>CartService: Сохранение приобретённых курсов

            loop Каждый приобретённый курс
                CartService->>Cart: removeCourse()
            end

            CartService->>CartService: Сброс скидки и сертификата

            loop Каждый приобретённый курс
                CartService->>Enrollment: notifyObservers(course)
                Enrollment->>Notification: update(course)
                Enrollment->>Logger: update(course)
            end

            CartService-->>CheckoutFacade: Итоговая стоимость
            CheckoutFacade-->>App: Результат покупки
            App-->>User: Покупка успешна

        else Заказ некорректен
            CartService-->>CheckoutFacade: IllegalArgumentException
            CheckoutFacade-->>App: IllegalArgumentException
            App-->>User: Сообщение об ошибке
        end

    else Пользователь отменяет покупку
        User->>App: Отмена
        App-->>User: Покупка отменена
    end
```

### Responsibilities

- App — взаимодействие с пользователем.
- CheckoutFacade — упрощение оформления покупки.
- CartService — бизнес-логика покупки.
- Cart — хранение выбранных курсов.
- DiscountStrategy — алгоритмы расчёта скидок.
- Enrollment — уведомление наблюдателей.
- CourseProgressService — управление прогрессом.
- CertificateService — выдача сертификатов.
- CourseRepository — доступ к каталогу курсов.

## Testing

Для проверки проекта используются:

- JUnit 5 — функциональное тестирование.
- ArchUnit — проверка архитектурных зависимостей.
- JaCoCo — анализ покрытия кода тестами.

Запуск всех тестов:

```bash
mvn clean test
```

Полная проверка проекта:

```bash
mvn clean verify
```

Основные тесты паттернов:

- CourseFactoryTest
- DiscountStrategyTest
- EnrollmentObserverTest
- EnrollmentStateTest
- CheckoutFacadeTest

Основной сценарий покупки проверяется
в PurchaseTest, AppTest и CheckoutFacadeTest.

## Team workflow

- One issue per task.
- One feature branch per issue.
- Pull request per branch.
- Automated tests before merge.
- One teammate review before merge.
- No direct commits to main.
