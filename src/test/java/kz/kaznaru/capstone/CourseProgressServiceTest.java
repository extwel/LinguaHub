
package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseProgress;
import kz.kaznaru.capstone.domain.LanguageCourse;
import kz.kaznaru.capstone.service.CourseProgressService;

class CourseProgressServiceTest {

    private CourseProgressService progressService;
    private Course course;

    @BeforeEach
    void setUp() {
        progressService = new CourseProgressService();

        course = new LanguageCourse(
                1,
                "English A1",
                "English",
                "Beginner",
                25000,
                40,
                "Beginner English course",
                true
        );
    }

    @Test
    void shouldOpenCourseAndCreateProgress() {
        CourseProgress progress = progressService.openCourse(course);

        assertNotNull(progress);
        assertEquals(1, progress.getCourseId());
        assertEquals(5, progress.getLessons().size());
        assertEquals(0, progress.getCompletedCount());
    }

    @Test
    void shouldReturnExistingProgressWhenCourseIsOpenedAgain() {
        CourseProgress first = progressService.openCourse(course);
        CourseProgress second = progressService.openCourse(course);

        assertSame(first, second);
    }

    @Test
    void shouldUpdateProgressAfterCompletingLesson() {
        progressService.openCourse(course);

        boolean result = progressService.completeLesson(1, 1);

        CourseProgress progress = progressService.getProgress(1);

        assertTrue(result);
        assertEquals(1, progress.getCompletedCount());
        assertEquals(4, progress.getRemainingCount());
        assertEquals(20.0, progress.getCompletionPercentage());
    }

    @Test
    void shouldNotCompleteLessonTwice() {
        progressService.openCourse(course);

        progressService.completeLesson(1, 1);
        boolean result = progressService.completeLesson(1, 1);

        assertFalse(result);
        assertEquals(1, progressService.getProgress(1).getCompletedCount());
    }

    @Test
    void shouldRejectNullCourse() {
        assertThrows(
                IllegalArgumentException.class,
                () -> progressService.openCourse(null)
        );
    }

    @Test
    void shouldRejectUnknownCourseProgress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> progressService.getProgress(999)
        );
    }
}
