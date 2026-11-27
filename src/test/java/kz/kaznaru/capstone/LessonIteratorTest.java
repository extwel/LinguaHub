package kz.kaznaru.capstone;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Lesson;
import kz.kaznaru.capstone.domain.LessonIterator;

class LessonIteratorTest {

    @Test
    void shouldIterateThroughLessonsInOrder() {
        Lesson first = new Lesson(1, "Первый урок");
        Lesson second = new Lesson(2, "Второй урок");
        Lesson third = new Lesson(3, "Третий урок");

        LessonIterator iterator =
                new LessonIterator(List.of(first, second, third));

        assertTrue(iterator.hasNext());
        assertSame(first, iterator.next());
        assertTrue(iterator.hasNext());
        assertSame(second, iterator.next());
        assertTrue(iterator.hasNext());
        assertSame(third, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void shouldThrowExceptionWhenNoLessonsRemain() {
        LessonIterator iterator =
                new LessonIterator(List.of(new Lesson(1, "Первый урок")));

        iterator.next();

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void shouldHandleEmptyList() {
        LessonIterator iterator = new LessonIterator(List.of());

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void shouldRejectNullList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LessonIterator(null)
        );
    }
}
