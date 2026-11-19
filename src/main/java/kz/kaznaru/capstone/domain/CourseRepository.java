package kz.kaznaru.capstone.domain;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    List<Course> findAll();

    Optional<Course> findById(long id);

    List<Course> findAvailableCourses();

    void save(Course course);
}
