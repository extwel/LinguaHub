
package kz.kaznaru.capstone.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseProgress;
import kz.kaznaru.capstone.domain.Lesson;

public class CourseProgressService {

    private final Map<Long, CourseProgress> progressMap = new HashMap<>();

    public CourseProgressService() {
    }

    public CourseProgress openCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Курс не может быть null");
        }

        return progressMap.computeIfAbsent(
                course.getId(),
                id -> new CourseProgress(id, createLessons(course))
        );
    }

    public CourseProgress getProgress(long courseId) {
        CourseProgress progress = progressMap.get(courseId);

        if (progress == null) {
            throw new IllegalArgumentException("Прогресс курса не найден");
        }

        return progress;
    }

    public boolean completeLesson(long courseId, long lessonId) {
        return getProgress(courseId).completeLesson(lessonId);
    }

    private List<Lesson> createLessons(Course course) {
        List<Lesson> lessons = new ArrayList<>();

        String[] lessonTitles = {
                "Введение в курс",
                "Основы и базовые понятия",
                "Практические упражнения",
                "Углублённое изучение",
                "Итоговый урок"
        };

        for (int i = 0; i < lessonTitles.length; i++) {
            lessons.add(new Lesson(i + 1, lessonTitles[i]));
        }

        return lessons;
    }
}
