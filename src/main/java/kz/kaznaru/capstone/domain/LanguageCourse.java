package kz.kaznaru.capstone.domain;

public class LanguageCourse extends Course {

    public LanguageCourse(
            long id,
            String title,
            String language,
            String level,
            double price,
            int durationHours,
            String description,
            boolean available
    ) {
        super(
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
