package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Enrollment {

    private final List<EnrollmentObserver> observers =
            new ArrayList<>();

    public void addObserver(EnrollmentObserver observer) {

        Objects.requireNonNull(observer);

        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(EnrollmentObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Course course) {

        Objects.requireNonNull(course);

        for (EnrollmentObserver observer : List.copyOf(observers)) {
            observer.update(course);
        }
    }
}
