package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseFactoryTest {

    @Test
    void languageFactoryCreatesLanguageCourse() {

        CourseFactory factory = new LanguageCourseFactory();

        Course course = factory.createCourse(
                1, "English A1", "English", "A1",
                25000, 40, "English course", true
        );

        assertInstanceOf(LanguageCourse.class, course);
        assertEquals("English", course.getLanguage());
    }

    @Test
    void examFactoryCreatesExamPrepCourse() {

        CourseFactory factory = new ExamPrepCourseFactory();

        Course course = factory.createCourse(
                2, "IELTS Preparation", "English", "B2",
                50000, 60, "IELTS course", true
        );

        assertInstanceOf(ExamPrepCourse.class, course);
        assertEquals("IELTS Preparation", course.getTitle());
    }

    @Test
    void factoryPreservesCourseData() {

        CourseFactory factory = new LanguageCourseFactory();

        Course course = factory.createCourse(
                3, "German A1", "German", "A1",
                30000, 45, "German course", true
        );

        assertEquals(3L, course.getId());
        assertEquals("German A1", course.getTitle());
        assertEquals("German", course.getLanguage());
        assertEquals("A1", course.getLevel());
        assertEquals(30000.0, course.getPrice());
        assertEquals(45, course.getDurationHours());
        assertEquals("German course", course.getDescription());
        assertTrue(course.isAvailable());
    }

    @Test
    void factoryCreatesUnavailableCourse() {

        CourseFactory factory = new ExamPrepCourseFactory();

        Course course = factory.createCourse(
                4, "IELTS", "English", "B2",
                60000, 80, "Exam course", false
        );

        assertFalse(course.isAvailable());
    }

    @Test
    void factoryRejectsNegativePrice() {

        CourseFactory factory = new LanguageCourseFactory();

        assertThrows(IllegalArgumentException.class,
                () -> factory.createCourse(
                        5, "English A1", "English", "A1",
                        -1000, 40, "English course", true
                ));
    }

    @Test
    void factoriesAreInterchangeable() {

        CourseFactory[] factories = {
                new LanguageCourseFactory(),
                new ExamPrepCourseFactory()
        };

        for (CourseFactory factory : factories) {

            Course course = factory.createCourse(
                    6, "English B2", "English", "B2",
                    45000, 60, "English course", true
            );

            assertNotNull(course);
            assertEquals("English B2", course.getTitle());
            assertEquals(45000.0, course.getPrice());
        }
    }
}
