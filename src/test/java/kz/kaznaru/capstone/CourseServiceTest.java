package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseFactory;
import kz.kaznaru.capstone.domain.LanguageCourseFactory;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.service.CourseService;

class CourseServiceTest {

    private InMemoryCourseRepository repository;
    private CourseService courseService;
    private CourseFactory factory;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCourseRepository();
        courseService = new CourseService(repository);
        factory = new LanguageCourseFactory();

        repository.save(factory.createCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Курс английского для начинающих", true));

        repository.save(factory.createCourse(
                2, "English B2", "English", "B2",
                45000, 60, "Продвинутый курс английского", false));
    }

    @Test
    void returnsAllCourses() {
        assertEquals(2, courseService.getAllCourses().size());
    }

    @Test
    void returnsOnlyAvailableCourses() {
        assertEquals(1, courseService.getAvailableCourses().size());
        assertEquals("English A1", courseService.getAvailableCourses().get(0).getTitle());
    }

    @Test
    void findsCourseById() {
        Course course = courseService.getCourseById(2);

        assertEquals("English B2", course.getTitle());
    }

    @Test
    void throwsExceptionWhenCourseNotFound() {
        assertThrows(
                IllegalArgumentException.class,
                () -> courseService.getCourseById(999));
    }

    @Test
    void findsCoursesByLanguageAndLevel() {
        var courses = courseService.findCoursesByLanguageAndLevel("English", "A1");

        assertEquals(1, courses.size());
        assertEquals("English A1", courses.get(0).getTitle());
    }

    @Test
    void searchIsCaseInsensitive() {
        var courses = courseService.findCoursesByLanguageAndLevel("english", "a1");

        assertEquals(1, courses.size());
        assertEquals("English A1", courses.get(0).getTitle());
    }

    @Test
    void returnsEmptyListWhenNoCoursesMatch() {
        var courses = courseService.findCoursesByLanguageAndLevel("German", "Beginner");

        assertEquals(0, courses.size());
    }

    @Test
    void returnsEmptyListWhenSearchParametersAreInvalid() {
        var courses = courseService.findCoursesByLanguageAndLevel("", null);

        assertEquals(0, courses.size());
    }
}
