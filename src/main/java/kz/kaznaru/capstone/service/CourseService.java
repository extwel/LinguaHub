package kz.kaznaru.capstone.service;

import java.util.List;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseRepository;

public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses();
    }

    public Course getCourseById(long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Курс с таким ID не найден"));
    }
}
