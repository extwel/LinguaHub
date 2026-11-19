package kz.kaznaru.capstone.infra;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseRepository;

public class InMemoryCourseRepository implements CourseRepository {

    private final Map<Long, Course> courses = new LinkedHashMap<>();

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses.values());
    }

    @Override
    public Optional<Course> findById(long id) {
        return Optional.ofNullable(courses.get(id));
    }

    @Override
    public List<Course> findAvailableCourses() {
        return courses.values()
                .stream()
                .filter(Course::isAvailable)
                .toList();
    }

    @Override
    public void save(Course course) {
        courses.put(course.getId(), course);
    }
}
