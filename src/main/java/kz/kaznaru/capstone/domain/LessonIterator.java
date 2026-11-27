
package kz.kaznaru.capstone.domain;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LessonIterator implements Iterator<Lesson> {

    private final List<Lesson> lessons;
    private int position = 0;

    public LessonIterator(List<Lesson> lessons) {
        if (lessons == null) {
            throw new IllegalArgumentException(
                    "Lessons list cannot be null");
        }

        this.lessons = List.copyOf(lessons);
    }

    @Override
    public boolean hasNext() {
        return position < lessons.size();
    }

    @Override
    public Lesson next() {
        if (!hasNext()) {
            throw new NoSuchElementException(
                    "No more lessons available");
        }

        return lessons.get(position++);
    }
}
