package kz.kaznaru.capstone;

import java.util.Scanner;

import kz.kaznaru.capstone.domain.CourseFactory;
import kz.kaznaru.capstone.domain.ExamPrepCourseFactory;
import kz.kaznaru.capstone.domain.LanguageCourseFactory;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.service.CourseService;
import kz.kaznaru.capstone.ui.CourseCatalogConsole;

public class App {

    public static void main(String[] args) {

        InMemoryCourseRepository repository = new InMemoryCourseRepository();

        CourseFactory languageFactory = new LanguageCourseFactory();
        CourseFactory examFactory = new ExamPrepCourseFactory();

        repository.save(languageFactory.createCourse(
                1,
                "English A1",
                "English",
                "Beginner",
                25000,
                40,
                "Базовый курс английского языка для начинающих.",
                true
        ));

        repository.save(languageFactory.createCourse(
                2,
                "English B2",
                "English",
                "Upper-Intermediate",
                45000,
                60,
                "Курс английского языка для продвинутого уровня.",
                true
        ));

        repository.save(languageFactory.createCourse(
                3,
                "German A1",
                "German",
                "Beginner",
                30000,
                45,
                "Начальный курс немецкого языка.",
                true
        ));

        repository.save(examFactory.createCourse(
                4,
                "IELTS Preparation",
                "English",
                "Advanced",
                60000,
                80,
                "Подготовка к международному экзамену IELTS.",
                false
        ));

        CourseService courseService = new CourseService(repository);

        CourseCatalogConsole catalogConsole = new CourseCatalogConsole(courseService);

        // Выводим весь каталог курсов
        catalogConsole.displayCatalog();

        // Создаем Scanner для ввода пользователя
        Scanner scanner = new Scanner(System.in);

        // Поиск курса
        System.out.println("\n--- Поиск курса ---");

        System.out.print("Введите язык курса: ");
        String language = scanner.nextLine();

        System.out.print("Введите уровень курса: ");
        String level = scanner.nextLine();

        // Выводим результаты поиска
        catalogConsole.searchCourses(language, level);

        scanner.close();
    }
}
