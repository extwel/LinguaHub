
package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseProgress {

    private final long courseId;
    private final List<Lesson> lessons;
    private final List<Long> completedLessonIds = new ArrayList<>();

    public CourseProgress(long courseId, List<Lesson> lessons) {
        if (courseId <= 0) {
            throw new IllegalArgumentException("ID курса должен быть положительным");
        }

        if (lessons == null || lessons.isEmpty()) {
            throw new IllegalArgumentException("Курс должен содержать уроки");
        }

        this.courseId = courseId;
        this.lessons = new ArrayList<>(lessons);
    }

    public long getCourseId() {
        return courseId;
    }

    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    public List<Long> getCompletedLessonIds() {
        return Collections.unmodifiableList(completedLessonIds);
    }

    public boolean completeLesson(long lessonId) {
        boolean lessonExists = lessons.stream()
                .anyMatch(lesson -> lesson.getId() == lessonId);

        if (!lessonExists) {
            throw new IllegalArgumentException("Урок не найден");
        }

        if (completedLessonIds.contains(lessonId)) {
            return false;
        }

        completedLessonIds.add(lessonId);
        return true;
    }

    public List<Lesson> getCompletedLessons() {
        return lessons.stream()
                .filter(lesson -> completedLessonIds.contains(lesson.getId()))
                .toList();
    }

    public List<Lesson> getRemainingLessons() {
        return lessons.stream()
                .filter(lesson -> !completedLessonIds.contains(lesson.getId()))
                .toList();
    }

    public int getCompletedCount() {
        return completedLessonIds.size();
    }

    public int getRemainingCount() {
        return lessons.size() - completedLessonIds.size();
    }

    public double getCompletionPercentage() {
        return (double) getCompletedCount() / lessons.size() * 100;
    }
}
