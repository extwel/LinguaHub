
package kz.kaznaru.capstone;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.CourseFactory;
import kz.kaznaru.capstone.domain.LanguageCourseFactory;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.service.CourseService;
import kz.kaznaru.capstone.ui.CourseCatalogConsole;

class CourseCatalogConsoleTest {

    @Test
    void displaysCourseCatalog() {
        InMemoryCourseRepository repository = new InMemoryCourseRepository();
        CourseFactory factory = new LanguageCourseFactory();

        repository.save(factory.createCourse(
                1, "English A1", "English", "Beginner",
                25000, 40, "Начальный курс английского", true));

        CourseService courseService = new CourseService(repository);
        CourseCatalogConsole console = new CourseCatalogConsole(courseService);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            console.displayCatalog();
        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString();

        assertTrue(result.contains("Каталог курсов"));
        assertTrue(result.contains("English A1"));
        assertTrue(result.contains("English"));
        assertTrue(result.contains("25,000 KZT"));
        assertTrue(result.contains("Доступен"));
    }

    @Test
    void displaysMessageWhenCatalogIsEmpty() {
        InMemoryCourseRepository repository = new InMemoryCourseRepository();
        CourseService courseService = new CourseService(repository);
        CourseCatalogConsole console = new CourseCatalogConsole(courseService);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            console.displayCatalog();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains("Каталог курсов пока пуст."));
    }

    @Test
    void displaysCourseDetails() {
        InMemoryCourseRepository repository = new InMemoryCourseRepository();
        CourseFactory factory = new LanguageCourseFactory();

        repository.save(factory.createCourse(
                1, "English A1", "English", "Beginner",
                25000, 40, "Начальный курс английского", true));

        CourseService courseService = new CourseService(repository);
        CourseCatalogConsole console = new CourseCatalogConsole(courseService);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            console.displayCourseDetails(1);
        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString();

        assertTrue(result.contains("Информация о курсе"));
        assertTrue(result.contains("English A1"));
        assertTrue(result.contains("Начальный курс английского"));
        assertTrue(result.contains("Доступен для покупки"));
        assertTrue(result.contains("25,000 KZT"));
    }

    @Test
    void displaysUnavailableCourseDetails() {
        InMemoryCourseRepository repository = new InMemoryCourseRepository();
        CourseFactory factory = new LanguageCourseFactory();

        repository.save(factory.createCourse(
                2, "IELTS Preparation", "English", "Advanced",
                60000, 80, "Подготовка к IELTS", false));

        CourseService courseService = new CourseService(repository);
        CourseCatalogConsole console = new CourseCatalogConsole(courseService);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            console.displayCourseDetails(2);
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains("Покупка недоступна"));
    }

    // US-02: проверка поиска курса по языку и уровню

    @Test
    void displaysSearchResultsWhenCourseIsFound() {
        InMemoryCourseRepository repository = new InMemoryCourseRepository();
        CourseFactory factory = new LanguageCourseFactory();

        repository.save(factory.createCourse(
                1, "English A1", "English", "Beginner",
                25000, 40, "Начальный курс английского", true));

        CourseService courseService = new CourseService(repository);
        CourseCatalogConsole console = new CourseCatalogConsole(courseService);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            console.searchCourses("English", "Beginner");
        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString();

        assertTrue(result.contains("Результаты поиска"));
        assertTrue(result.contains("English A1"));
        assertTrue(result.contains("English"));
        assertTrue(result.contains("Beginner"));
        assertTrue(result.contains("25,000 KZT"));
    }

    @Test
    void displaysUnavailableCourseInSearchResults() {
        InMemoryCourseRepository repository = new InMemoryCourseRepository();
        CourseFactory factory = new LanguageCourseFactory();

        repository.save(factory.createCourse(
                4, "IELTS Preparation", "English", "Advanced",
                60000, 80, "Подготовка к IELTS", false));

        CourseService courseService = new CourseService(repository);
        CourseCatalogConsole console = new CourseCatalogConsole(courseService);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            console.searchCourses("English", "Advanced");
        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString();

        assertTrue(result.contains("IELTS Preparation"));
        assertTrue(result.contains("Advanced"));
        assertTrue(result.contains("Недоступен"));
    }

    @Test
    void displaysMessageWhenSearchReturnsNoCourses() {
        InMemoryCourseRepository repository = new InMemoryCourseRepository();
        CourseService courseService = new CourseService(repository);
        CourseCatalogConsole console = new CourseCatalogConsole(courseService);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            console.searchCourses("French", "Beginner");
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains(
                "Курсы по заданным параметрам не найдены."));
    }
}
