
package kz.kaznaru.capstone.domain;

public interface CourseAccess {

    CourseProgress openCourse(Course course);

    boolean completeLesson(long courseId, long lessonId);

}
