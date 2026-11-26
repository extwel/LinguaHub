package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.LanguageCourse;
import kz.kaznaru.capstone.domain.CompletionCertificate;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CourseProgressService;
import kz.kaznaru.capstone.service.CertificateService;

class CertificateServiceTest {

    private CartService cartService;
    private CourseProgressService progressService;
    private CertificateService certificateService;
    private Course course;

    @BeforeEach
    void setUp() {

        cartService = new CartService(new Cart());

        progressService = new CourseProgressService();

        certificateService = new CertificateService(
                cartService, progressService);

        course = new LanguageCourse(
                1, "English A1", "English", "Beginner",
                25000, 40, "English course", true
        );
    }

    private void purchaseCourse() {
        cartService.addCourse(course);
        cartService.purchaseCourses();
        progressService.openCourse(course);
    }

    private void completeAllLessons() {
        for (int i = 1; i <= 5; i++) {
            progressService.completeLesson(1, i);
        }
    }

    @Test
    void shouldIssueCertificateAfterCompletion() {

        purchaseCourse();
        completeAllLessons();

        CompletionCertificate certificate =
                certificateService.issueCertificate(
                        1, "Student");

        assertEquals("Student",
                certificate.getStudentName());

        assertEquals("English A1",
                certificate.getCourseName());

        assertEquals(LocalDate.now(),
                certificate.getIssueDate());

        assertTrue(certificateService.hasCertificate(1));
    }

    @Test
    void shouldRejectIncompleteCourse() {

        purchaseCourse();

        progressService.completeLesson(1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> certificateService.issueCertificate(
                        1, "Student"));

        assertFalse(certificateService.hasCertificate(1));
    }

    @Test
    void shouldRejectUnpurchasedCourse() {

        assertThrows(IllegalArgumentException.class,
                () -> certificateService.issueCertificate(
                        1, "Student"));
    }

    @Test
    void shouldRejectCourseWithoutCompletedLessons() {

        purchaseCourse();

        assertThrows(IllegalArgumentException.class,
                () -> certificateService.issueCertificate(
                        1, "Student"));
    }

    @Test
    void shouldRejectEmptyStudentName() {

        purchaseCourse();
        completeAllLessons();

        assertThrows(IllegalArgumentException.class,
                () -> certificateService.issueCertificate(
                        1, ""));

        assertFalse(certificateService.hasCertificate(1));
    }

    @Test
    void shouldRejectNullStudentName() {

        assertThrows(IllegalArgumentException.class,
                () -> new CompletionCertificate(
                        null, "English A1"));
    }

    @Test
    void shouldRejectEmptyCourseName() {

        assertThrows(IllegalArgumentException.class,
                () -> new CompletionCertificate(
                        "Student", ""));
    }

    @Test
    void shouldRejectNullCourseName() {

        assertThrows(IllegalArgumentException.class,
                () -> new CompletionCertificate(
                        "Student", null));
    }

    @Test
    void shouldReturnExistingCertificate() {

        purchaseCourse();
        completeAllLessons();

        CompletionCertificate first =
                certificateService.issueCertificate(
                        1, "Student");

        CompletionCertificate second =
                certificateService.issueCertificate(
                        1, "Student");

        assertSame(first, second);
    }

    @Test
    void shouldAllowViewingCertificate() {

        purchaseCourse();
        completeAllLessons();

        CompletionCertificate issued =
                certificateService.issueCertificate(
                        1, "Student");

        assertSame(issued,
                certificateService.getCertificate(1));
    }

    @Test
    void shouldRejectMissingCertificate() {

        assertFalse(certificateService.hasCertificate(1));

        assertThrows(IllegalArgumentException.class,
                () -> certificateService.getCertificate(1));
    }

    @Test
    void shouldRejectCourseWithOneRemainingLesson() {

        purchaseCourse();

        for (int i = 1; i <= 4; i++) {
            progressService.completeLesson(1, i);
        }

        assertThrows(IllegalArgumentException.class,
                () -> certificateService.issueCertificate(
                        1, "Student"));
    }
}
