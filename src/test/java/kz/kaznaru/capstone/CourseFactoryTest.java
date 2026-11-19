package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseFactory;
import kz.kaznaru.capstone.domain.ExamPrepCourse;
import kz.kaznaru.capstone.domain.ExamPrepCourseFactory;
import kz.kaznaru.capstone.domain.LanguageCourse;
import kz.kaznaru.capstone.domain.LanguageCourseFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseFactoryTest {

    @Test
    void languageFactoryCreatesLanguageCourse() {
        CourseFactory factory = new LanguageCourseFactory();

        Course course = factory.createCourse(
                1,
                "English for Beginners",
                "English",
                "A1",
                25000,
                40,
                "Курс английского языка для начинающих",
                true
        );

        assertInstanceOf(LanguageCourse.class, course);
        assertEquals("English", course.getLanguage());
    }

    @Test
    void examFactoryCreatesExamPrepCourse() {
        CourseFactory factory = new ExamPrepCourseFactory();

        Course course = factory.createCourse(
                2,
                "IELTS Preparation",
                "English",
                "B2",
                50000,
                60,
                "Подготовка к экзамену IELTS",
                true
        );

        assertInstanceOf(ExamPrepCourse.class, course);
        assertEquals("IELTS Preparation", course.getTitle());
    }
}
