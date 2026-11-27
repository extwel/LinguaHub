
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseFactory;
import kz.kaznaru.capstone.domain.LanguageCourseFactory;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;

class CourseRepositoryTest {

    private InMemoryCourseRepository repository;
    private CourseFactory factory;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCourseRepository();
        factory = new LanguageCourseFactory();
    }

    private Course createCourse(
            long id,
            String title,
            boolean available) {

        return factory.createCourse(
                id,
                title,
                "English",
                "A1",
                25000,
                40,
                "English course",
                available
        );
    }

    @Test
    void savesAndFindsCourseById() {
        Course course = createCourse(1, "English A1", true);

        repository.save(course);

        assertTrue(repository.findById(1).isPresent());
        assertEquals("English A1", repository.findById(1).get().getTitle());
    }

    @Test
    void returnsEmptyWhenCourseDoesNotExist() {
        assertFalse(repository.findById(999).isPresent());
    }

    @Test
    void returnsAllSavedCourses() {
        repository.save(createCourse(1, "English A1", true));
        repository.save(createCourse(2, "English B2", false));

        List<Course> courses = repository.findAll();

        assertEquals(2, courses.size());
    }

    @Test
    void returnsOnlyAvailableCourses() {
        repository.save(createCourse(1, "English A1", true));
        repository.save(createCourse(2, "English B2", false));

        List<Course> courses = repository.findAvailableCourses();

        assertEquals(1, courses.size());
        assertEquals("English A1", courses.get(0).getTitle());
    }

    @Test
    void replacesCourseWhenSavingSameId() {
        repository.save(createCourse(1, "English A1", true));
        repository.save(createCourse(1, "English A2", false));

        assertEquals(1, repository.findAll().size());
        assertEquals("English A2", repository.findById(1).get().getTitle());
    }

    @Test
    void returnsEmptyListsWhenRepositoryIsEmpty() {
        assertTrue(repository.findAll().isEmpty());
        assertTrue(repository.findAvailableCourses().isEmpty());
    }
}
