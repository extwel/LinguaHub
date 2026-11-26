
package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {

    private final List<Course> courses = new ArrayList<>();

    public boolean addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Курс не может быть пустым");
        }

        if (!course.isAvailable()) {
            throw new IllegalArgumentException("Этот курс недоступен для покупки");
        }

        boolean alreadyExists = courses.stream()
                .anyMatch(existingCourse -> existingCourse.getId() == course.getId());

        if (alreadyExists) {
            return false;
        }

        courses.add(course);
        return true;
    }

    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    public double getTotalPrice() {
        return courses.stream()
                .mapToDouble(Course::getPrice)
                .sum();
    }
}
