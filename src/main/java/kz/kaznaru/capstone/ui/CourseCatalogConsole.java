package kz.kaznaru.capstone.ui;

import java.util.List;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.service.CourseService;

public class CourseCatalogConsole {

    private final CourseService courseService;

    public CourseCatalogConsole(CourseService courseService) {
        this.courseService = courseService;
    }

    public void displayCatalog() {
        List<Course> courses = courseService.getAllCourses();

        System.out.println("\n========== LinguaHub ==========");
        System.out.println("        Каталог курсов");
        System.out.println("==============================");

        if (courses.isEmpty()) {
            System.out.println("Каталог курсов пока пуст.");
            return;
        }

        for (Course course : courses) {
            System.out.println("\nID: " + course.getId());
            System.out.println("Название: " + course.getTitle());
            System.out.println("Язык: " + course.getLanguage());
            System.out.println("Уровень: " + course.getLevel());
            System.out.println("Цена: " + course.getPrice() + " ₸");
            System.out.println("Длительность: " + course.getDurationHours() + " часов");
            System.out.println("Статус: " + (course.isAvailable() ? "Доступен" : "Недоступен"));
        }

        System.out.println("\n==============================");
    }

    public void displayCourseDetails(long courseId) {
        Course course = courseService.getCourseById(courseId);

        System.out.println("\n======= Информация о курсе =======");
        System.out.println("Название: " + course.getTitle());
        System.out.println("Язык: " + course.getLanguage());
        System.out.println("Уровень: " + course.getLevel());
        System.out.println("Цена: " + course.getPrice() + " ₸");
        System.out.println("Длительность: " + course.getDurationHours() + " часов");
        System.out.println("Описание: " + course.getDescription());
        System.out.println("Статус: " + (course.isAvailable() ? "Доступен для покупки" : "Покупка недоступна"));
        System.out.println("=================================");
    }

    public void searchCourses(String language, String level) {
        List<Course> courses = courseService.findCoursesByLanguageAndLevel(language, level);

        System.out.println("\n======= Результаты поиска =======");

        if (courses.isEmpty()) {
            System.out.println("Курсы по заданным параметрам не найдены.");
            return;
        }

        for (Course course : courses) {
            System.out.println("\nID: " + course.getId());
            System.out.println("Название: " + course.getTitle());
            System.out.println("Язык: " + course.getLanguage());
            System.out.println("Уровень: " + course.getLevel());
            System.out.println("Цена: " + course.getPrice() + " ₸");
            System.out.println("Длительность: " + course.getDurationHours() + " часов");
            System.out.println("Статус: " + (course.isAvailable() ? "Доступен" : "Недоступен"));
        }

        System.out.println("\n================================");
    }
}
