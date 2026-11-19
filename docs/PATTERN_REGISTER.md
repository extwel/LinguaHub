# Pattern Register

Fill one row per pattern **actually used**. Each row must point to real classes and a test.
Delete the TODO rows as you replace them. `scripts/check-deliverables.sh` counts rows.

| # | Pattern        | Lecture    | Problem in this project (the smell it removes)
|---|----------------|------------|-----------------------------------------------|-------------|------|------------------------|
| 1 | Factory Method | practice 1 | Позволяет избежать дублирования логики создания курса и прямой зависимости от конкретных классов курсов в приложении                   |
| 2 | Strategy       | practice 4 | Удаляет крупные если/иначе или переключатели, используемые для расчёта различных типов скидок                                          | 
| 3 | Observer       | practice 4 | Блокировка логики регистрации, которая не может быть тесно привязана к службам уведомления                                             | 
| 4 | State          | practice 5 | Удаляет разрозненные условия для проверки статуса регистрации и управления допустимыми переходами                                      | 
| 5 | Facade         | practice 3 | Предотвращает управление процессом оплаты посредством нескольких сложных вызовов на обслуживание прямо из пользовательского интерфейса | 

|     Patterns       |                                     Key classes                                                         |           Test          |          Alternative considered        |
| 1 | Factory Method | Course, LanguageCourse, ExamPrepCourse, CourseFactory                                                   | CourseFactoryTest       | 	Constructor / direct object creation  |
| 2 | Strategy       | DiscountStrategy, PercentageDiscount, FixedDiscount, DiscountService                                    | DiscountServiceTest     |  if/else conditional logic             |
| 3 | Observer       | Enrollment, EnrollmentObserver, EnrollmentNotification, EnrollmentLogger                                | EnrollmentObserverTest  | 	Direct method calls                   |
| 4 | State          | Enrollment, EnrollmentState, PendingState, ActiveState, CompletedState, CancelledState                  | EnrollmentStateTest     | 	Enum + if/else                        |
| 5 | Facade         | CheckoutFacade, CartService, DiscountService, GiftSertificateService, PaymentService, EnrollmentService | CheckoutFacadeTest      |  Direct calls to multiple services     |





## Rejected patterns
At least two patterns you considered and decided NOT to use, with the reason (YAGNI). Link the ADR.

|      Pattern     | Why rejected | ADR |
|------------------|--------------|-----|
| Abstract Factory | LinguaHub не нуждается в создании семейств связанных объектов. Фабричного метода достаточно для создания различных типов курсов        | ADR-0002 |
| Builder          | Объекты курса имеют достаточно простую структуру и не требуют сложной многоступенчатой постройки или множества опциональных параметров | ADR-0002 |
| Mediator         | Нынешняя система не имеет сложных взаимодействий между компонентами, которые оправдывают введение центрального посредника              | ADR-0002 |
