
package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.*;
import kz.kaznaru.capstone.service.CartService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentObserverTest {

    private Course createCourse() {

        return new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "English course", true
        );
    }

    @Test
    void notificationReceivesEvent() {

        Enrollment enrollment = new Enrollment();

        EnrollmentNotification notification =
                new EnrollmentNotification();

        enrollment.addObserver(notification);
        enrollment.notifyObservers(createCourse());

        assertEquals(1, notification.getNotifications().size());

        assertTrue(notification.getNotifications()
                .get(0).contains("English A1"));
    }

    @Test
    void loggerReceivesEvent() {

        Enrollment enrollment = new Enrollment();
        EnrollmentLogger logger = new EnrollmentLogger();

        enrollment.addObserver(logger);
        enrollment.notifyObservers(createCourse());

        assertEquals(1, logger.getLogs().size());
    }

    @Test
    void multipleObserversReceiveEvent() {

        Enrollment enrollment = new Enrollment();

        EnrollmentNotification notification =
                new EnrollmentNotification();

        EnrollmentLogger logger = new EnrollmentLogger();

        enrollment.addObserver(notification);
        enrollment.addObserver(logger);

        enrollment.notifyObservers(createCourse());

        assertEquals(1, notification.getNotifications().size());
        assertEquals(1, logger.getLogs().size());
    }

    @Test
    void removedObserverDoesNotReceiveEvent() {

        Enrollment enrollment = new Enrollment();

        EnrollmentNotification notification =
                new EnrollmentNotification();

        enrollment.addObserver(notification);
        enrollment.removeObserver(notification);

        enrollment.notifyObservers(createCourse());

        assertTrue(notification.getNotifications().isEmpty());
    }

    @Test
    void duplicateObserverReceivesOnlyOneEvent() {

        Enrollment enrollment = new Enrollment();

        EnrollmentNotification notification =
                new EnrollmentNotification();

        enrollment.addObserver(notification);
        enrollment.addObserver(notification);

        enrollment.notifyObservers(createCourse());

        assertEquals(1, notification.getNotifications().size());
    }

    @Test
    void purchaseNotifiesObservers() {

        CartService service = new CartService(new Cart());

        service.addCourse(createCourse());
        service.purchaseCourses();

        assertEquals(1, service.getEnrollmentNotifications().size());
        assertEquals(1, service.getEnrollmentLogs().size());

        assertTrue(service.isPurchased(1));
    }

    @Test
    void failedPurchaseDoesNotNotifyObservers() {

        CartService service = new CartService(new Cart());

        assertThrows(
                IllegalArgumentException.class,
                service::purchaseCourses
        );

        assertTrue(service.getEnrollmentNotifications().isEmpty());
        assertTrue(service.getEnrollmentLogs().isEmpty());
    }
}
