
package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Enrollment {

    // Observer
    private final List<EnrollmentObserver> observers =
            new ArrayList<>();

    // State
    private EnrollmentState state = new PendingState();

    void setState(EnrollmentState state) {
        this.state = Objects.requireNonNull(state);
    }

    public EnrollmentState getState() {
        return state;
    }

    public String getStatus() {
        return state.getName();
    }

    public boolean canContinueLearning() {
        return state.canContinueLearning();
    }

    public void activate() {
        state.activate(this);
    }

    public void complete() {
        state.complete(this);
    }

    public void cancel() {
        state.cancel(this);
    }

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
