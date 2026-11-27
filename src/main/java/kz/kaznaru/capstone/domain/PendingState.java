package kz.kaznaru.capstone.domain;

public class PendingState implements EnrollmentState {

    @Override
    public String getName() {
        return "PENDING";
    }

    @Override
    public void activate(Enrollment enrollment) {
        enrollment.setState(new ActiveState());
    }

    @Override
    public void cancel(Enrollment enrollment) {
        enrollment.setState(new CancelledState());
    }
}
