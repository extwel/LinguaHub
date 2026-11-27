
package kz.kaznaru.capstone.domain;

public class SpeakingCourseFactory extends CourseFactory {

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
        return new SpeakingCourse(
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
