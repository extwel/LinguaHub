
package kz.kaznaru.capstone;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.CourseProgress;
import kz.kaznaru.capstone.domain.Lesson;

class CourseProgressTest {

    private CourseProgress progress;

    @BeforeEach
    void setUp() {
        List<Lesson> lessons = List.of(
                new Lesson(1, "Введение в курс"),
                new Lesson(2, "Основы и базовые понятия"),
                new Lesson(3, "Практические упражнения"),
                new Lesson(4, "Углублённое изучение"),
                new Lesson(5, "Итоговый урок")
        );

        progress = new CourseProgress(1, lessons);
    }

    @Test
    void shouldStartWithZeroProgress() {
        assertEquals(0, progress.getCompletedCount());
        assertEquals(5, progress.getRemainingCount());
        assertEquals(0.0, progress.getCompletionPercentage());
    }

    @Test
    void shouldCompleteLesson() {
        boolean result = progress.completeLesson(1);

        assertTrue(result);
        assertEquals(1, progress.getCompletedCount());
        assertEquals(4, progress.getRemainingCount());
        assertEquals(20.0, progress.getCompletionPercentage());
    }

    @Test
    void shouldNotCompleteSameLessonTwice() {
        progress.completeLesson(1);

        boolean result = progress.completeLesson(1);

        assertFalse(result);
        assertEquals(1, progress.getCompletedCount());
    }

    @Test
    void shouldReturnCompletedAndRemainingLessons() {
        progress.completeLesson(1);
        progress.completeLesson(2);

        assertEquals(2, progress.getCompletedLessons().size());
        assertEquals(3, progress.getRemainingLessons().size());
    }

    @Test
    void shouldReachOneHundredPercentAfterCompletingAllLessons() {
        for (long i = 1; i <= 5; i++) {
            progress.completeLesson(i);
        }

        assertEquals(5, progress.getCompletedCount());
        assertEquals(0, progress.getRemainingCount());
        assertEquals(100.0, progress.getCompletionPercentage());
    }

    @Test
    void shouldRejectUnknownLesson() {
        assertThrows(
                IllegalArgumentException.class,
                () -> progress.completeLesson(99)
        );
    }
}
