
package kz.kaznaru.capstone.domain;

public class Lesson {

    private final long id;
    private final String title;

    public Lesson(long id, String title) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID урока должен быть положительным");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название урока не может быть пустым");
        }

        this.id = id;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
