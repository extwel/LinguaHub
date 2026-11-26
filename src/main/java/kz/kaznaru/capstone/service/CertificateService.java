package kz.kaznaru.capstone.service;

import java.util.HashMap;
import java.util.Map;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseProgress;
import kz.kaznaru.capstone.domain.CompletionCertificate;

public class CertificateService {

    private final CartService cartService;
    private final CourseProgressService progressService;

    private final Map<Long, CompletionCertificate> certificates =
            new HashMap<>();

    public CertificateService(
            CartService cartService,
            CourseProgressService progressService) {

        this.cartService = cartService;
        this.progressService = progressService;
    }

    public CompletionCertificate issueCertificate(
            long courseId, String studentName) {

        Course course = cartService.getPurchasedCourses()
                .stream()
                .filter(c -> c.getId() == courseId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Вы не приобрели этот курс"));

        CourseProgress progress =
                progressService.openCourse(course);

        if (!progress.getRemainingLessons().isEmpty()) {
            throw new IllegalArgumentException(
                    "Сначала завершите все уроки курса");
        }

        return certificates.computeIfAbsent(
                courseId,
                id -> new CompletionCertificate(
                        studentName,
                        course.getTitle())
        );
    }

    public boolean hasCertificate(long courseId) {
        return certificates.containsKey(courseId);
    }

    public CompletionCertificate getCertificate(long courseId) {

        CompletionCertificate certificate =
                certificates.get(courseId);

        if (certificate == null) {
            throw new IllegalArgumentException(
                    "Сертификат ещё не получен");
        }

        return certificate;
    }
}
