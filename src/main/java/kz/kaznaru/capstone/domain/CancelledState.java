package kz.kaznaru.capstone.domain;

public class CancelledState implements EnrollmentState {

    @Override
    public String getName() {
        return "CANCELLED";
    }
}
