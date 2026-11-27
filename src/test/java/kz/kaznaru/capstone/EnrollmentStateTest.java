
package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.*;
import kz.kaznaru.capstone.service.CourseProgressService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentStateTest {

    @Test
    void initialStateIsPending() {

        Enrollment enrollment = new Enrollment();

        assertInstanceOf(
                PendingState.class, enrollment.getState()
        );

        assertEquals("PENDING", enrollment.getStatus());
    }

    @Test
    void pendingCanBecomeActive() {

        Enrollment enrollment = new Enrollment();
        enrollment.activate();

        assertInstanceOf(
                ActiveState.class, enrollment.getState()
        );
    }

    @Test
    void activeCanBecomeCompleted() {

        Enrollment enrollment = new Enrollment();

        enrollment.activate();
        enrollment.complete();

        assertInstanceOf(
                CompletedState.class, enrollment.getState()
        );
    }

    @Test
    void pendingCanBeCancelled() {

        Enrollment enrollment = new Enrollment();
        enrollment.cancel();

        assertInstanceOf(
                CancelledState.class, enrollment.getState()
        );
    }

    @Test
    void pendingCannotBeCompleted() {

        Enrollment enrollment = new Enrollment();

        assertThrows(
                IllegalStateException.class,
                enrollment::complete
        );
    }

    @Test
    void completedCannotBeActivated() {

        Enrollment enrollment = new Enrollment();

        enrollment.activate();
        enrollment.complete();

        assertThrows(
                IllegalStateException.class,
                enrollment::activate
        );
    }

    @Test
    void cancelledCannotBeActivated() {

        Enrollment enrollment = new Enrollment();
        enrollment.cancel();

        assertThrows(
                IllegalStateException.class,
                enrollment::activate
        );
    }

    @Test
    void activeCannotBeCancelled() {

        Enrollment enrollment = new Enrollment();
        enrollment.activate();

        assertThrows(
                IllegalStateException.class,
                enrollment::cancel
        );
    }

    @Test
    void completedCannotBeCompletedAgain() {

        Enrollment enrollment = new Enrollment();

        enrollment.activate();
        enrollment.complete();

        assertThrows(
                IllegalStateException.class,
                enrollment::complete
        );
    }

    @Test
    void courseCompletionChangesState() {

        CourseProgressService service =
                new CourseProgressService();

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "English course", true
        );

        service.openCourse(course);

        assertEquals(
                "ACTIVE", service.getEnrollmentStatus(1)
        );

        for (int i = 1; i <= 5; i++) {
            service.completeLesson(1, i);
        }

        assertEquals(
                "COMPLETED", service.getEnrollmentStatus(1)
        );
    }
}
