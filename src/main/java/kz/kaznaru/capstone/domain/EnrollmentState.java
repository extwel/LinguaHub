package kz.kaznaru.capstone.domain;

public interface EnrollmentState {

    String getName();

    default boolean canContinueLearning() {
        return false;
    }

    default void activate(Enrollment enrollment) {
        throw new IllegalStateException(
                "Активация невозможна");
    }

    default void complete(Enrollment enrollment) {
        throw new IllegalStateException(
                "Завершение невозможно");
    }

    default void cancel(Enrollment enrollment) {
        throw new IllegalStateException(
                "Отмена невозможна");
    }
}
