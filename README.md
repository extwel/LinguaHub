# LinguaHub

> Capstone project — Application Design Based on Patterns
> (Design Patterns), 4th year, Information Systems.

## Team

- Ayan — extwel
- Aniyar — Aniyar1

## Domain

LinguaHub — онлайн-рынок языковых курсов, позволяющий
пользователям просматривать и приобретать курсы,
добавлять их в корзину, применять скидки, использовать
подарочные сертификаты и получать сертификаты после
завершения обучения.

## Quick start

```bash
mvn verify
mvn exec:java -Dexec.mainClass=kz.kaznaru.capstone.App
```

## Architecture

Проект разделён на четыре основных слоя:

- domain — классы предметной области и интерфейсы.
- service — бизнес-логика приложения.
- infra — реализация репозиториев и хранение данных.
- ui — консольный интерфейс пользователя.

### Class diagram

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
        +getDurationHours() int
        +getDescription() String
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

    class CourseCatalogConsole {
        +displayCatalog() void
        +displayCourseDetails(long courseId) void
        +searchCourses(String language, String level) void
    }

    class App {
        +main(String[] args) void
    }

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

## Implemented patterns

- **Factory Method** — создание различных типов курсов.
- **Repository** — отделение бизнес-логики от хранения данных.

## Planned patterns

- **Strategy** — различные алгоритмы расчёта скидок.
- **Observer** — уведомление компонентов о событиях.
- **State** — управление статусами обучения.
- **Facade** — упрощение процесса оформления заказа.

## Team workflow

Branch per issue, pull request per branch,
one teammate review before merge.
