package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentNotification implements EnrollmentObserver {

    private final List<String> notifications =
            new ArrayList<>();

    @Override
    public void update(Course course) {

        notifications.add(
                "Вы успешно зарегистрированы на курс: "
                        + course.getTitle()
        );
    }

    public List<String> getNotifications() {
        return List.copyOf(notifications);
    }
}
