package kz.kaznaru.capstone.domain;

public interface EnrollmentState {

    String getName();

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
