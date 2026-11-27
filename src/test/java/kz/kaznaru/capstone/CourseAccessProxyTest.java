
package kz.kaznaru.capstone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import kz.kaznaru.capstone.domain.*;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CourseAccessProxy;
import kz.kaznaru.capstone.service.CourseProgressService;

class CourseAccessProxyTest {

    private CartService cartService;
    private CourseProgressService realService;
    private CourseAccess proxy;
    private Course firstCourse;
    private Course secondCourse;

    @BeforeEach
    void setUp() {

        cartService = new CartService(new Cart());

        realService = new CourseProgressService();

        proxy = new CourseAccessProxy(
                cartService, realService);

        firstCourse = new LanguageCourse(
                1,
                "English A1",
                "English",
                "Beginner",
                25000,
                40,
                "English course",
                true
        );

        secondCourse = new LanguageCourse(
                2,
                "German A1",
                "German",
                "Beginner",
                30000,
                45,
                "German course",
                true
        );
    }

    private void purchaseFirstCourse() {

        cartService.addCourse(firstCourse);
        cartService.purchaseCourses();
    }

    @Test
    void shouldRejectUnpurchasedCourse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.openCourse(firstCourse)
        );
    }

    @Test
    void shouldOpenPurchasedCourse() {

        purchaseFirstCourse();

        CourseProgress progress =
                proxy.openCourse(firstCourse);

        assertNotNull(progress);

        assertEquals(
                1L,
                progress.getCourseId()
        );

        assertEquals(
                5,
                progress.getLessons().size()
        );
    }

    @Test
    void shouldRejectCourseOnlyAddedToCart() {

        cartService.addCourse(firstCourse);

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.openCourse(firstCourse)
        );
    }

    @Test
    void shouldRejectAnotherUnpurchasedCourse() {

        purchaseFirstCourse();

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.openCourse(secondCourse)
        );
    }

    @Test
    void shouldRejectNullCourse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.openCourse(null)
        );
    }

    @Test
    void shouldReuseExistingCourseProgress() {

        purchaseFirstCourse();

        CourseProgress firstProgress =
                proxy.openCourse(firstCourse);

        CourseProgress secondProgress =
                proxy.openCourse(firstCourse);

        assertSame(
                firstProgress,
                secondProgress
        );
    }

    @Test
    void shouldCompleteLessonForPurchasedCourse() {

        purchaseFirstCourse();

        CourseProgress progress =
                proxy.openCourse(firstCourse);

        assertTrue(
                proxy.completeLesson(1, 1)
        );

        assertEquals(
                1,
                progress.getCompletedCount()
        );

        assertEquals(
                20.0,
                progress.getCompletionPercentage()
        );
    }

    @Test
    void shouldRejectLessonCompletionForUnpurchasedCourse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.completeLesson(1, 1)
        );
    }

    @Test
    void shouldRejectInvalidCourseId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.completeLesson(0, 1)
        );
    }

    @Test
    void shouldRejectInvalidLessonId() {

        purchaseFirstCourse();

        proxy.openCourse(firstCourse);

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.completeLesson(1, 99)
        );
    }

    @Test
    void shouldPreserveRepeatedLessonBehavior() {

        purchaseFirstCourse();

        proxy.openCourse(firstCourse);

        assertTrue(
                proxy.completeLesson(1, 1)
        );

        assertFalse(
                proxy.completeLesson(1, 1)
        );
    }

    @Test
    void shouldNotCreateProgressWhenAccessDenied() {

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.openCourse(firstCourse)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> realService.getProgress(1)
        );
    }

    @Test
    void shouldRejectNullCartService() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CourseAccessProxy(
                        null, realService)
        );
    }

    @Test
    void shouldRejectNullRealAccess() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CourseAccessProxy(
                        cartService, null)
        );
    }

    @Test
    void shouldDelegateAllowedOperationToRealService() {

        purchaseFirstCourse();

        CourseProgress progress =
                proxy.openCourse(firstCourse);

        assertSame(
                progress,
                realService.getProgress(1)
        );
    }

    @Test
    void shouldUseActuallyPurchasedCourse() {

        purchaseFirstCourse();

        Course anotherObject = new LanguageCourse(
                1,
                "Another title",
                "English",
                "Beginner",
                100,
                10,
                "Another description",
                true
        );

        CourseAccess recordingAccess = new CourseAccess() {

            @Override
            public CourseProgress openCourse(Course course) {

                assertSame(firstCourse, course);

                return realService.openCourse(course);
            }

            @Override
            public boolean completeLesson(
                    long courseId, long lessonId) {

                return realService.completeLesson(
                        courseId, lessonId);
            }
        };

        CourseAccess protectedAccess =
                new CourseAccessProxy(
                        cartService, recordingAccess);

        assertNotNull(
                protectedAccess.openCourse(anotherObject)
        );
    }

    @Test
    void shouldNotDelegateDeniedOperations() {

        CourseAccess forbiddenAccess = new CourseAccess() {

            @Override
            public CourseProgress openCourse(Course course) {

                fail("Запрещённая операция передана сервису");

                return null;
            }

            @Override
            public boolean completeLesson(
                    long courseId, long lessonId) {

                fail("Запрещённая операция передана сервису");

                return false;
            }
        };

        CourseAccess protectedAccess =
                new CourseAccessProxy(
                        cartService, forbiddenAccess);

        assertThrows(
                IllegalArgumentException.class,
                () -> protectedAccess.openCourse(firstCourse)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> protectedAccess.completeLesson(1, 1)
        );
    }
}
