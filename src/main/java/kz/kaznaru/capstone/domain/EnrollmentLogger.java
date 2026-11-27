package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentLogger implements EnrollmentObserver {

    private final List<String> logs =
            new ArrayList<>();

    @Override
    public void update(Course course) {

        logs.add(
                "Регистрация на курс ID: "
                        + course.getId()
                        + " — " + course.getTitle()
        );
    }

    public List<String> getLogs() {
        return List.copyOf(logs);
    }
}
