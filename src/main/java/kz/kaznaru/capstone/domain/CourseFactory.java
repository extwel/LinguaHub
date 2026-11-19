package kz.kaznaru.capstone.domain;

public abstract class CourseFactory {

    public abstract Course createCourse(
            long id,
            String title,
            String language,
            String level,
            double price,
            int durationHours,
            String description,
            boolean available
    );
}
