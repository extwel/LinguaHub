package kz.kaznaru.capstone;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseFactory;
import kz.kaznaru.capstone.domain.ExamPrepCourseFactory;
import kz.kaznaru.capstone.domain.LanguageCourseFactory;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CourseService;
import kz.kaznaru.capstone.ui.CourseCatalogConsole;

public class App {

    private static final NumberFormat PRICE_FORMAT =
            NumberFormat.getNumberInstance(Locale.US);

    private static String formatPrice(double price) {
        return PRICE_FORMAT.format(price) + " KZT";
    }

    public static void main(String[] args) {

        InMemoryCourseRepository repository = new InMemoryCourseRepository();

        CourseFactory languageFactory = new LanguageCourseFactory();
        CourseFactory examFactory = new ExamPrepCourseFactory();

        repository.save(languageFactory.createCourse(
                1, "English A1", "English", "Beginner",
                25000, 40,
                "Базовый курс английского языка для начинающих.", true
        ));

        repository.save(languageFactory.createCourse(
                2, "English B2", "English", "Upper-Intermediate",
                45000, 60,
                "Курс английского языка для продвинутого уровня.", true
        ));

        repository.save(languageFactory.createCourse(
                3, "German A1", "German", "Beginner",
                30000, 45,
                "Начальный курс немецкого языка.", true
        ));

        repository.save(examFactory.createCourse(
                4, "IELTS Preparation", "English", "Advanced",
                60000, 80,
                "Подготовка к международному экзамену IELTS.", false
        ));

        CourseService courseService = new CourseService(repository);
        CourseCatalogConsole catalogConsole =
                new CourseCatalogConsole(courseService);

        Cart cart = new Cart();
        CartService cartService = new CartService(cart);

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
            System.out.println("0. Завершить работу");
            System.out.println("=============================");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();

            switch (choice) {

                // US-03: Добавление курса
                case "1":
                    try {
                        System.out.print("Введите ID курса: ");
                        long courseId = Long.parseLong(scanner.nextLine());

                        Course course = courseService.getCourseById(courseId);

                        boolean added = cartService.addCourse(course);

                        if (added) {
                            System.out.println(
                                    "Курс успешно добавлен в корзину!"
                            );
                        } else {
                            System.out.println(
                                    "Этот курс уже находится в корзине."
                            );
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Ошибка: введите корректный ID."
                        );
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;

                // Просмотр корзины
                case "2":
                    System.out.println("\n======= Ваша корзина =======");

                    if (cartService.getCourses().isEmpty()) {
                        System.out.println("Корзина пока пуста.");
                    } else {

                        for (Course course : cartService.getCourses()) {
                            System.out.println(
                                    course.getId() + ". "
                                    + course.getTitle() + " — "
                                    + formatPrice(course.getPrice())
                            );
                        }

                        System.out.println("----------------------------");
                        System.out.println(
                                "Итого: "
                                + formatPrice(cartService.getTotalPrice())
                        );
                    }
                    break;

                // US-04: Удаление курса
                case "3":
                    try {
                        System.out.print("Введите ID курса для удаления: ");
                        long courseId = Long.parseLong(scanner.nextLine());

                        boolean removed = cartService.removeCourse(courseId);

                        if (removed) {
                            System.out.println(
                                    "Курс успешно удалён из корзины!"
                            );

                            System.out.println(
                                    "Итого: "
                                    + formatPrice(cartService.getTotalPrice())
                            );
                        } else {
                            System.out.println(
                                    "Такого курса нет в корзине."
                            );
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Ошибка: введите корректный ID."
                        );
                    }
                    break;

                case "0":
                    running = false;
                    System.out.println(
                            "Спасибо за использование LinguaHub!"
                    );
                    break;

                default:
                    System.out.println(
                            "Неизвестная команда. Попробуйте снова."
                    );
            }
        }

        scanner.close();
    }
}
