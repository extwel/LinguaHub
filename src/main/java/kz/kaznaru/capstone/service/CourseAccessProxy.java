
package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseAccess;
import kz.kaznaru.capstone.domain.CourseProgress;

public class CourseAccessProxy implements CourseAccess {

    private final CartService cartService;
    private final CourseAccess realAccess;

    public CourseAccessProxy(
            CartService cartService,
            CourseAccess realAccess) {

        if (cartService == null) {
            throw new IllegalArgumentException(
                    "CartService не может быть null");
        }

        if (realAccess == null) {
            throw new IllegalArgumentException(
                    "CourseAccess не может быть null");
        }

        this.cartService = cartService;
        this.realAccess = realAccess;
    }

    private void checkAccess(long courseId) {

        if (courseId <= 0) {
            throw new IllegalArgumentException(
                    "Некорректный ID курса");
        }

        if (!cartService.isPurchased(courseId)) {
            throw new IllegalArgumentException(
                    "Доступ запрещён: сначала приобретите курс");
        }
    }

    @Override
    public CourseProgress openCourse(Course course) {

        if (course == null) {
            throw new IllegalArgumentException(
                    "Курс не может быть null");
        }

        checkAccess(course.getId());

        Course purchasedCourse =
                cartService.getPurchasedCourses()
                        .stream()
                        .filter(c -> c.getId() == course.getId())
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Курс не найден среди ваших покупок"));

        return realAccess.openCourse(purchasedCourse);
    }

    @Override
    public boolean completeLesson(
            long courseId, long lessonId) {

        checkAccess(courseId);

        return realAccess.completeLesson(
                courseId, lessonId);
    }
}
