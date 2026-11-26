package kz.kaznaru.capstone.domain;

import java.time.LocalDate;

public class CompletionCertificate {

    private final String studentName;
    private final String courseName;
    private final LocalDate issueDate;

    public CompletionCertificate(
            String studentName,
            String courseName) {

        if (studentName == null || studentName.isBlank()) {
            throw new IllegalArgumentException(
                    "Имя пользователя не может быть пустым");
        }

        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException(
                    "Название курса не может быть пустым");
        }

        this.studentName = studentName.trim();
        this.courseName = courseName;
        this.issueDate = LocalDate.now();
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }
}
