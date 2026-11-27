package kz.kaznaru.capstone.domain;

public class ActiveState implements EnrollmentState {

    @Override
    public String getName() {
        return "ACTIVE";
    }

    @Override
    public boolean canContinueLearning() {
        return true;
    }

    @Override
    public void complete(Enrollment enrollment) {
        enrollment.setState(new CompletedState());
    }
}
