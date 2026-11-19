package kz.kaznaru.capstone.domain;

public class ExamPrepCourseFactory extends CourseFactory {

    @Override
    public Course createCourse(
            long id,
            String title,
            String language,
            String level,
            double price,
            int durationHours,
            String description,
            boolean available
    ) {
        return new ExamPrepCourse(
                id,
                title,
                language,
                level,
                price,
                durationHours,
                description,
                available
        );
    }
}
