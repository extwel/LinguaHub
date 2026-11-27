
package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.LanguageCourse;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CourseAccessProxy;
import kz.kaznaru.capstone.service.CourseProgressService;

class ProxyStateCombinationTest {

    private CartService cartService;
    private CourseProgressService progressService;
    private CourseAccessProxy proxy;
    private Course course;

    @BeforeEach
    void setUp() {

        cartService = new CartService(new Cart());
        progressService = new CourseProgressService();

        proxy = new CourseAccessProxy(
                cartService,
                progressService
        );

        course = new LanguageCourse(
                1,
                "English A1",
                "English",
                "Beginner",
                25000,
                40,
                "English course",
                true
        );
    }

    private void purchaseCourse() {

        cartService.addCourse(course);
        cartService.purchaseCourses();
    }

    @Test
    void proxyShouldDenyAccessToUnpurchasedCourse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.openCourse(course)
        );
    }

    @Test
    void purchasedCourseShouldOpenWithActiveEnrollment() {

        purchaseCourse();

        assertNotNull(proxy.openCourse(course));

        assertEquals(
                "ACTIVE",
                progressService.getEnrollmentStatus(1)
        );
    }

    @Test
    void activeEnrollmentShouldAllowLessonCompletion() {

        purchaseCourse();
        proxy.openCourse(course);

        assertTrue(proxy.completeLesson(1, 1));

        assertEquals(
                "ACTIVE",
                progressService.getEnrollmentStatus(1)
        );
    }

    @Test
    void completingAllLessonsShouldChangeEnrollmentToCompleted() {

        purchaseCourse();
        proxy.openCourse(course);

        for (long lessonId = 1; lessonId <= 5; lessonId++) {
            assertTrue(
                    proxy.completeLesson(1, lessonId)
            );
        }

        assertEquals(
                "COMPLETED",
                progressService.getEnrollmentStatus(1)
        );
    }

    @Test
    void completedEnrollmentShouldRejectFurtherLearning() {

        purchaseCourse();
        proxy.openCourse(course);

        for (long lessonId = 1; lessonId <= 5; lessonId++) {
            proxy.completeLesson(1, lessonId);
        }

        assertThrows(
                IllegalStateException.class,
                () -> proxy.completeLesson(1, 1)
        );

        assertEquals(
                "COMPLETED",
                progressService.getEnrollmentStatus(1)
        );
    }
}
