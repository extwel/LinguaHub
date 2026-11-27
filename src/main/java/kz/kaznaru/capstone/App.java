
package kz.kaznaru.capstone;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.CheckoutSummary;
import kz.kaznaru.capstone.domain.CompletionCertificate;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseAccess;
import kz.kaznaru.capstone.domain.CourseFactory;
import kz.kaznaru.capstone.domain.CourseProgress;
import kz.kaznaru.capstone.domain.Discount;
import kz.kaznaru.capstone.domain.DiscountType;
import kz.kaznaru.capstone.domain.ExamPrepCourseFactory;
import kz.kaznaru.capstone.domain.LanguageCourseFactory;
import kz.kaznaru.capstone.domain.Lesson;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CertificateService;
import kz.kaznaru.capstone.service.CourseAccessProxy;
import kz.kaznaru.capstone.service.CourseProgressService;
import kz.kaznaru.capstone.service.CourseService;
import kz.kaznaru.capstone.ui.CourseCatalogConsole;

public class App {

    private static final NumberFormat PRICE_FORMAT =
            NumberFormat.getNumberInstance(Locale.US);

    private static String formatPrice(double price) {
        return PRICE_FORMAT.format(price) + " KZT";
    }

    public static void main(String[] args) {

        InMemoryCourseRepository repository =
                new InMemoryCourseRepository();

        CourseFactory languageFactory =
                new LanguageCourseFactory();

        CourseFactory examFactory =
                new ExamPrepCourseFactory();

        repository.save(languageFactory.createCourse(
                1, "English A1", "English", "Beginner",
                25000, 40,
                "Базовый курс английского языка для начинающих.",
                true
        ));

        repository.save(languageFactory.createCourse(
                2, "English B2", "English", "Upper-Intermediate",
                45000, 60,
                "Курс английского языка для продвинутого уровня.",
                true
        ));

        repository.save(languageFactory.createCourse(
                3, "German A1", "German", "Beginner",
                30000, 45,
                "Начальный курс немецкого языка.",
                true
        ));

        repository.save(examFactory.createCourse(
                4, "IELTS Preparation", "English", "Advanced",
                60000, 80,
                "Подготовка к международному экзамену IELTS.",
                false
        ));

        CourseService courseService =
                new CourseService(repository);

        CourseCatalogConsole catalogConsole =
                new CourseCatalogConsole(courseService);

        Cart cart = new Cart();

        CartService cartService =
                new CartService(cart);

        CourseProgressService progressService =
                new CourseProgressService();

        // M3-03: Proxy
        CourseAccess courseAccess =
                new CourseAccessProxy(
                        cartService, progressService);

        CertificateService certificateService =
                new CertificateService(cartService, progressService);

        Scanner scanner = new Scanner(System.in);

        catalogConsole.displayCatalog();

        System.out.println("\n--- Поиск курса ---");

        System.out.print("Введите язык курса: ");
        String language = scanner.nextLine();

        System.out.print("Введите уровень курса: ");
        String level = scanner.nextLine();

        catalogConsole.searchCourses(language, level);

        boolean running = true;

        while (running) {

            System.out.println("\n========== Корзина ==========");
            System.out.println("1. Добавить курс в корзину");
            System.out.println("2. Посмотреть корзину");
            System.out.println("3. Удалить курс из корзины");
            System.out.println("4. Применить скидку");
            System.out.println("5. Отменить скидку");
            System.out.println("6. Применить подарочный сертификат");
            System.out.println("7. Оформить покупку");
            System.out.println("8. Мои курсы");
            System.out.println("9. Отслеживать прогресс курса");
            System.out.println("10. Получить сертификат");
            System.out.println("0. Завершить работу");
            System.out.println("=============================");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();

            switch (choice) {

                // US-03
                case "1":
                    try {
                        System.out.print("Введите ID курса: ");

                        long courseId =
                                Long.parseLong(scanner.nextLine());

                        Course course =
                                courseService.getCourseById(courseId);

                        boolean added =
                                cartService.addCourse(course);

                        if (added) {
                            System.out.println(
                                    "Курс успешно добавлен в корзину!");
                        } else {
                            System.out.println(
                                    "Этот курс уже находится в корзине.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Ошибка: введите корректный ID.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(
                                "Ошибка: " + e.getMessage());
                    }

                    break;

                // Просмотр корзины
                case "2":

                    System.out.println(
                            "\n======= Ваша корзина =======");

                    if (cartService.getCourses().isEmpty()) {

                        System.out.println("Корзина пока пуста.");

                    } else {

                        for (Course course : cartService.getCourses()) {

                            System.out.println(
                                    course.getId() + ". "
                                            + course.getTitle() + " - "
                                            + formatPrice(course.getPrice())
                            );
                        }

                        System.out.println("----------------------------");

                        System.out.println(
                                "Первоначальная цена: "
                                        + formatPrice(
                                                cartService.getTotalPrice())
                        );

                        System.out.println(
                                "Скидка: "
                                        + formatPrice(
                                                cartService.getDiscountAmount())
                        );

                        System.out.println(
                                "Подарочный сертификат: "
                                        + formatPrice(
                                                cartService.getGiftCertificateAmount())
                        );

                        System.out.println(
                                "Итого к оплате: "
                                        + formatPrice(
                                                cartService.getFinalPrice())
                        );
                    }

                    break;

                // US-04
                case "3":

                    try {

                        System.out.print(
                                "Введите ID курса для удаления: ");

                        long courseId =
                                Long.parseLong(scanner.nextLine());

                        boolean removed =
                                cartService.removeCourse(courseId);

                        if (removed) {

                            System.out.println(
                                    "Курс успешно удалён из корзины!");

                            System.out.println(
                                    "Итого: "
                                            + formatPrice(
                                                    cartService.getFinalPrice())
                            );

                        } else {

                            System.out.println(
                                    "Такого курса нет в корзине.");
                        }

                    } catch (NumberFormatException e) {

                        System.out.println(
                                "Ошибка: введите корректный ID.");
                    }

                    break;

                // US-05
                case "4":

                    try {

                        if (cartService.getCourses().isEmpty()) {

                            System.out.println(
                                    "Сначала добавьте курс в корзину.");

                            break;
                        }

                        System.out.println("\nВыберите тип скидки:");
                        System.out.println("1. Процентная скидка");
                        System.out.println("2. Фиксированная скидка");
                        System.out.print("Ваш выбор: ");

                        String discountChoice = scanner.nextLine();

                        DiscountType type;

                        if (discountChoice.equals("1")) {

                            type = DiscountType.PERCENTAGE;

                            System.out.print(
                                    "Введите процент скидки: ");

                        } else if (discountChoice.equals("2")) {

                            type = DiscountType.FIXED;

                            System.out.print(
                                    "Введите сумму скидки в KZT: ");

                        } else {

                            System.out.println(
                                    "Неизвестный тип скидки.");

                            break;
                        }

                        double value =
                                Double.parseDouble(scanner.nextLine());

                        Discount discount =
                                new Discount(type, value);

                        cartService.applyDiscount(discount);

                        System.out.println(
                                "Скидка успешно применена!");

                        System.out.println(
                                "Размер скидки: "
                                        + formatPrice(
                                                cartService.getDiscountAmount())
                        );

                        System.out.println(
                                "Итого к оплате: "
                                        + formatPrice(
                                                cartService.getFinalPrice())
                        );

                    } catch (NumberFormatException e) {

                        System.out.println(
                                "Ошибка: введите корректное число.");

                    } catch (IllegalArgumentException e) {

                        System.out.println(
                                "Ошибка: " + e.getMessage());
                    }

                    break;

                // Отмена скидки
                case "5":

                    cartService.removeDiscount();

                    System.out.println("Скидка отменена.");

                    System.out.println(
                            "Итого: "
                                    + formatPrice(
                                            cartService.getFinalPrice())
                    );

                    break;

                // US-06
                case "6":

                    try {

                        System.out.print(
                                "Введите код подарочного сертификата: ");

                        String code = scanner.nextLine();

                        cartService.applyGiftCertificate(code);

                        System.out.println(
                                "Сертификат успешно применён!");

                        System.out.println(
                                "Сумма сертификата: "
                                        + formatPrice(
                                                cartService.getGiftCertificateAmount())
                        );

                        System.out.println(
                                "Итого к оплате: "
                                        + formatPrice(
                                                cartService.getFinalPrice())
                        );

                    } catch (IllegalArgumentException e) {

                        System.out.println(
                                "Ошибка: " + e.getMessage());
                    }

                    break;

                // US-07, US-08
                case "7":

                    if (cartService.getCourses().isEmpty()) {

                        System.out.println(
                                "Корзина пуста. Добавьте курс перед покупкой.");

                        break;
                    }

                    try {

                        // AUDIT-02: прямая работа с CartService
                        CheckoutSummary summary =
                                cartService.prepareCheckout();

                        System.out.println(
                                "\n======= Подтверждение покупки =======");

                        System.out.println(
                                "Первоначальная стоимость: "
                                        + formatPrice(summary.totalPrice())
                        );

                        System.out.println(
                                "Скидка: "
                                        + formatPrice(summary.discountAmount())
                        );

                        System.out.println(
                                "Подарочный сертификат: "
                                        + formatPrice(summary.certificateAmount())
                        );

                        System.out.println("------------------------------------");

                        System.out.println(
                                "ИТОГО К ОПЛАТЕ: "
                                        + formatPrice(summary.finalPrice())
                        );

                        System.out.println("------------------------------------");

                        System.out.print(
                                "Подтвердить покупку? (1 - Да, 0 - Нет): ");

                        String confirmation = scanner.nextLine();

                        if (confirmation.equals("1")) {

                            double paid =
                                    cartService.purchaseCourses();

                            System.out.println(
                                    "Покупка успешно подтверждена!");

                            System.out.println(
                                    "Сумма покупки: " + formatPrice(paid));

                            System.out.println(
                                    "Вы успешно зарегистрированы на курсах!");

                            System.out.println("Корзина очищена.");

                        } else {

                            System.out.println("Покупка отменена.");
                        }

                    } catch (IllegalArgumentException e) {

                        System.out.println(
                                "Ошибка: " + e.getMessage());
                    }

                    break;

                // US-08: Мои курсы
                case "8":

                    System.out.println(
                            "\n========== Мои курсы ==========");

                    if (cartService.getPurchasedCourses().isEmpty()) {

                        System.out.println(
                                "У вас пока нет купленных курсов.");

                    } else {

                        for (Course course :
                                cartService.getPurchasedCourses()) {

                            System.out.println(
                                    "ID: " + course.getId());

                            System.out.println(
                                    "Название: " + course.getTitle());

                            System.out.println(
                                    "Язык: " + course.getLanguage());

                            System.out.println(
                                    "Уровень: " + course.getLevel());

                            System.out.println(
                                    "Статус: Вы зарегистрированы");

                            System.out.println("----------------------------");
                        }
                    }

                    break;

                // US-09: Отслеживание прогресса курса
                case "9":

                    List<Course> purchasedCourses =
                            cartService.getPurchasedCourses();

                    if (purchasedCourses.isEmpty()) {

                        System.out.println(
                                "Сначала приобретите курс.");

                        break;
                    }

                    System.out.println(
                            "\n======= Выберите курс =======");

                    for (Course course : purchasedCourses) {

                        System.out.println(
                                course.getId() + ". "
                                        + course.getTitle());
                    }

                    System.out.print("Введите ID курса: ");

                    try {

                        long courseId =
                                Long.parseLong(scanner.nextLine());

                        Course selectedCourse =
                                courseService.getCourseById(courseId);

                        // M3-03: открытие курса через Proxy
                        CourseProgress progress =
                                courseAccess.openCourse(selectedCourse);

                        boolean courseMenu = true;

                        while (courseMenu) {

                            System.out.println(
                                    "\n======= " + selectedCourse.getTitle()
                                            + " =======");

                            System.out.println(
                                    "Завершено уроков: "
                                            + progress.getCompletedCount());

                            System.out.println(
                                    "Осталось уроков: "
                                            + progress.getRemainingCount());

                            System.out.printf(
                                    Locale.US,
                                    "Прогресс курса: %.1f%%%n",
                                    progress.getCompletionPercentage());

                            System.out.println("\nСписок уроков:");

                            // M3-02: Iterator
                            for (Lesson lesson : progress) {

                                boolean completed =
                                        progress.getCompletedLessonIds()
                                                .contains(lesson.getId());

                                System.out.println(
                                        lesson.getId() + ". "
                                                + lesson.getTitle()
                                                + (completed
                                                ? " [Завершён]"
                                                : " [Не завершён]")
                                );
                            }

                            System.out.println(
                                    "\n1. Отметить урок завершённым");

                            System.out.println(
                                    "0. Вернуться в главное меню");

                            System.out.print("Выберите действие: ");

                            String progressChoice = scanner.nextLine();

                            if (progressChoice.equals("1")) {

                                System.out.print("Введите ID урока: ");

                                try {

                                    long lessonId =
                                            Long.parseLong(scanner.nextLine());

                                    // M3-03: завершение урока через Proxy
                                    boolean completed =
                                            courseAccess.completeLesson(
                                                    courseId, lessonId);

                                    if (completed) {

                                        System.out.println(
                                                "Урок успешно завершён!");

                                    } else {

                                        System.out.println(
                                                "Этот урок уже завершён.");
                                    }

                                    if (progress.getCompletionPercentage()
                                            == 100.0) {

                                        System.out.println(
                                                "Поздравляем! Вы завершили курс!");
                                    }

                                } catch (NumberFormatException e) {

                                    System.out.println(
                                            "Ошибка: введите корректный ID урока.");

                                } catch (IllegalArgumentException e) {

                                    System.out.println(
                                            "Ошибка: " + e.getMessage());
                                }

                            } else if (progressChoice.equals("0")) {

                                courseMenu = false;

                            } else {

                                System.out.println(
                                        "Неизвестная команда.");
                            }
                        }

                    } catch (NumberFormatException e) {

                        System.out.println(
                                "Ошибка: введите корректный ID курса.");

                    } catch (IllegalArgumentException e) {

                        System.out.println(
                                "Ошибка: " + e.getMessage());
                    }

                    break;

                // US-10: Получение сертификата
                case "10":

                    List<Course> purchasedCoursesForCert =
                            cartService.getPurchasedCourses();

                    if (purchasedCoursesForCert.isEmpty()) {

                        System.out.println(
                                "Сначала приобретите курс.");

                        break;
                    }

                    System.out.println(
                            "\n======= Выберите курс для получения сертификата =======");

                    for (Course course : purchasedCoursesForCert) {

                        System.out.println(
                                course.getId() + ". "
                                        + course.getTitle());
                    }

                    System.out.print("Введите ID курса: ");

                    try {

                        long courseId =
                                Long.parseLong(scanner.nextLine());

                        CompletionCertificate certificate =
                                certificateService.issueCertificate(
                                        courseId, "Student Name");

                        System.out.println(
                                "Поздравляем! Вы получили сертификат о завершении курса.");

                        System.out.println(
                                "Имя студента: "
                                        + certificate.getStudentName());

                        System.out.println(
                                "Название курса: "
                                        + certificate.getCourseName());

                    } catch (NumberFormatException e) {

                        System.out.println(
                                "Ошибка: введите корректный ID курса.");

                    } catch (IllegalArgumentException e) {

                        System.out.println(
                                "Ошибка: " + e.getMessage());
                    }

                    break;

                case "0":

                    running = false;

                    System.out.println(
                            "Спасибо за использование LinguaHub!");

                    break;

                default:

                    System.out.println(
                            "Неизвестная команда. Попробуйте снова.");
            }
        }

        scanner.close();
    }
}
