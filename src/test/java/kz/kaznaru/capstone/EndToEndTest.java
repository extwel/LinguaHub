
package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.CheckoutSummary;
import kz.kaznaru.capstone.domain.CompletionCertificate;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseProgress;
import kz.kaznaru.capstone.domain.Discount;
import kz.kaznaru.capstone.domain.DiscountType;
import kz.kaznaru.capstone.domain.LanguageCourse;

import kz.kaznaru.capstone.infra.InMemoryCourseRepository;

import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CertificateService;
import kz.kaznaru.capstone.service.CourseProgressService;
import kz.kaznaru.capstone.service.CourseService;

class EndToEndTest {

    @Test
    void shouldCompleteFullCoursePurchaseAndLearningJourney() {

        // 1. Создаём каталог и добавляем курс English A1
        InMemoryCourseRepository repository =
                new InMemoryCourseRepository();

        Course course = new LanguageCourse(
                1,
                "English A1",
                "English",
                "Beginner",
                25000,
                40,
                "English course",
                true
        );

        repository.save(course);

        CourseService courseService =
                new CourseService(repository);

        // 2. Ищем курс по языку и уровню
        var searchResults =
                courseService.findCoursesByLanguageAndLevel(
                        "English",
                        "Beginner"
                );

        assertEquals(1, searchResults.size());
        assertEquals("English A1", searchResults.get(0).getTitle());
        assertEquals(25000.0, searchResults.get(0).getPrice());

        // 3. Добавляем курс в корзину
        CartService cartService =
                new CartService(new Cart());

        assertTrue(cartService.addCourse(searchResults.get(0)));
        assertEquals(25000.0, cartService.getTotalPrice());

        // 4. Применяем скидку 10% и сертификат GIFT5000
        cartService.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10)
        );

        cartService.applyGiftCertificate("GIFT5000");

        CheckoutSummary summary =
                cartService.prepareCheckout();

        assertEquals(25000.0, summary.totalPrice());
        assertEquals(2500.0, summary.discountAmount());
        assertEquals(5000.0, summary.certificateAmount());
        assertEquals(17500.0, summary.finalPrice());

        // 5. Подтверждаем покупку
        double paidAmount = cartService.purchaseCourses();

        assertEquals(17500.0, paidAmount);
        assertTrue(cartService.isPurchased(1));
        assertTrue(cartService.getCourses().isEmpty());

        // 6. Проверяем My Courses
        assertEquals(1, cartService.getPurchasedCourses().size());

        assertEquals(
                "English A1",
                cartService.getPurchasedCourses().get(0).getTitle()
        );

        // 7. Проверяем уведомления Observer и Logger
        assertEquals(1, cartService.getEnrollmentNotifications().size());
        assertEquals(1, cartService.getEnrollmentLogs().size());

        assertTrue(
                cartService.getEnrollmentNotifications()
                        .get(0).contains("English A1")
        );

        // 8. Открываем приобретённый курс
        CourseProgressService progressService =
                new CourseProgressService();

        CourseProgress progress =
                progressService.openCourse(course);

        assertEquals(
                "ACTIVE",
                progressService.getEnrollmentStatus(1)
        );

        assertEquals(5, progress.getLessons().size());

        // 9. Завершаем все пять уроков
        for (int lessonId = 1; lessonId <= 5; lessonId++) {

            assertTrue(
                    progressService.completeLesson(1, lessonId)
            );
        }

        // 10. Проверяем завершение обучения
        assertEquals(5, progress.getCompletedCount());
        assertEquals(0, progress.getRemainingCount());
        assertEquals(100.0, progress.getCompletionPercentage());

        assertEquals(
                "COMPLETED",
                progressService.getEnrollmentStatus(1)
        );

        // 11. Получаем сертификат
        CertificateService certificateService =
                new CertificateService(
                        cartService,
                        progressService
                );

        CompletionCertificate certificate =
                certificateService.issueCertificate(
                        1,
                        "Student"
                );

        assertNotNull(certificate);
        assertEquals("Student", certificate.getStudentName());
        assertEquals("English A1", certificate.getCourseName());
        assertTrue(certificateService.hasCertificate(1));
    }
}
