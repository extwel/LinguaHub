
package kz.kaznaru.capstone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import kz.kaznaru.capstone.domain.*;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CourseService;
import kz.kaznaru.capstone.service.CourseAccessProxy;
import kz.kaznaru.capstone.service.CourseProgressService;
import kz.kaznaru.capstone.service.CertificateService;

class ExtensionPointTest {

    private Course createSpeakingCourse() {

        CourseFactory factory =
                new SpeakingCourseFactory();

        return factory.createCourse(
                5,
                "English Speaking B1",
                "English",
                "Intermediate",
                35000,
                50,
                "Курс разговорного английского языка.",
                true
        );
    }

    @Test
    void shouldCreateNewCourseType() {

        Course course = createSpeakingCourse();

        assertInstanceOf(
                SpeakingCourse.class,
                course
        );
    }

    @Test
    void shouldPreserveCourseData() {

        Course course = createSpeakingCourse();

        assertEquals(5L, course.getId());

        assertEquals(
                "English Speaking B1",
                course.getTitle()
        );

        assertEquals(
                "English",
                course.getLanguage()
        );

        assertEquals(
                "Intermediate",
                course.getLevel()
        );

        assertEquals(
                35000.0,
                course.getPrice()
        );

        assertEquals(
                50,
                course.getDurationHours()
        );

        assertEquals(
                "Курс разговорного английского языка.",
                course.getDescription()
        );

        assertTrue(course.isAvailable());
    }

    @Test
    void shouldRejectNegativePrice() {

        CourseFactory factory =
                new SpeakingCourseFactory();

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.createCourse(
                        5,
                        "Speaking",
                        "English",
                        "Intermediate",
                        -100,
                        50,
                        "Speaking course",
                        true
                )
        );
    }

    @Test
    void shouldRejectInvalidDuration() {

        CourseFactory factory =
                new SpeakingCourseFactory();

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.createCourse(
                        5,
                        "Speaking",
                        "English",
                        "Intermediate",
                        35000,
                        0,
                        "Speaking course",
                        true
                )
        );
    }

    @Test
    void shouldSaveNewCourseInExistingRepository() {

        CourseRepository repository =
                new InMemoryCourseRepository();

        Course course = createSpeakingCourse();

        repository.save(course);

        assertSame(
                course,
                repository.findById(5).orElseThrow()
        );
    }

    @Test
    void shouldFindNewCourseUsingExistingService() {

        CourseRepository repository =
                new InMemoryCourseRepository();

        repository.save(createSpeakingCourse());

        CourseService service =
                new CourseService(repository);

        assertEquals(
                1,
                service.findCoursesByLanguageAndLevel(
                        "English",
                        "Intermediate"
                ).size()
        );

        assertInstanceOf(
                SpeakingCourse.class,
                service.getCourseById(5)
        );
    }

    @Test
    void shouldPurchaseNewCourseUsingExistingCartService() {

        CartService cartService =
                new CartService(new Cart());

        Course course = createSpeakingCourse();

        assertTrue(
                cartService.addCourse(course)
        );

        assertEquals(
                35000.0,
                cartService.purchaseCourses()
        );

        assertTrue(
                cartService.isPurchased(5)
        );

        assertTrue(
                cartService.getCourses().isEmpty()
        );
    }

    @Test
    void shouldApplyExistingDiscountToNewCourse() {

        CartService cartService =
                new CartService(new Cart());

        cartService.addCourse(createSpeakingCourse());

        cartService.applyDiscount(
                new Discount(
                        DiscountType.PERCENTAGE,
                        10
                )
        );

        assertEquals(
                31500.0,
                cartService.purchaseCourses()
        );

        assertTrue(
                cartService.isPurchased(5)
        );
    }

    @Test
    void shouldProtectNewCourseUsingExistingProxy() {

        CartService cartService =
                new CartService(new Cart());

        CourseProgressService progressService =
                new CourseProgressService();

        CourseAccess proxy =
                new CourseAccessProxy(
                        cartService,
                        progressService
                );

        Course course = createSpeakingCourse();

        assertThrows(
                IllegalArgumentException.class,
                () -> proxy.openCourse(course)
        );

        cartService.addCourse(course);
        cartService.purchaseCourses();

        assertNotNull(
                proxy.openCourse(course)
        );
    }

    @Test
    void shouldSupportCompleteLearningScenario() {

        Course course = createSpeakingCourse();

        CartService cartService =
                new CartService(new Cart());

        CourseProgressService progressService =
                new CourseProgressService();

        CourseAccess proxy =
                new CourseAccessProxy(
                        cartService,
                        progressService
                );

        CertificateService certificateService =
                new CertificateService(
                        cartService,
                        progressService
                );

        cartService.addCourse(course);
        cartService.purchaseCourses();

        CourseProgress progress =
                proxy.openCourse(course);

        assertEquals(
                "ACTIVE",
                progressService.getEnrollmentStatus(5)
        );

        // Iterator + Proxy + State
        for (Lesson lesson : progress) {

            assertTrue(
                    proxy.completeLesson(
                            course.getId(),
                            lesson.getId()
                    )
            );
        }

        assertEquals(
                100.0,
                progress.getCompletionPercentage()
        );

        assertEquals(
                "COMPLETED",
                progressService.getEnrollmentStatus(5)
        );

        CompletionCertificate certificate =
                certificateService.issueCertificate(
                        5,
                        "Test Student"
                );

        assertEquals(
                "Test Student",
                certificate.getStudentName()
        );

        assertEquals(
                "English Speaking B1",
                certificate.getCourseName()
        );
    }
}
