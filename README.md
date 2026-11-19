# <LinguaHub>

> Capstone project — Application Design Based on Patterns (Design Patterns), 4th year, Information Systems.

**Team:** <Ayan, Aniyar and extwel, Aniyar1>
**Domain:** <LinguaHub - это онлайн-рынок для языковых курсов. Он позволяет пользователям просматривать курсы, добавлять их в корзину, применять скидки, использовать подарочные сертификаты, покупать курсы и получать сертификаты после завершения учебы.>

## Quick start
```bash
mvn verify                      # build + tests + coverage gate
mvn exec:java -Dexec.mainClass=kz.kaznaru.capstone.App   # or press F5 in VS Code
```

## Architecture
<!-- Insert a Mermaid class/component diagram here. --> 
```mermaid
flowchart TD
    UI[Presentation Layer - Console UI]
    BL[Business Logic Layer - Services]
    D[Domain - Course, Enrollment, Order]
    DAL[Data Access Layer - Repositories]
    DB[(Storage)]

    UI --> BL
    BL --> D
    BL --> DAL
    DAL --> DB

## Patterns used
Factory Method - Создание различных типов курсов
Strategy - Внедрение различных алгоритмов расчета скидки
Observer - Уведомления компонентов о событиях регистрации
State - Управление статусами регистрации и действительными переходами
Facade - Упрощение процесса оформления курса


## Team workflow
Branch per issue, pull request per branch, one teammate review before merge.
