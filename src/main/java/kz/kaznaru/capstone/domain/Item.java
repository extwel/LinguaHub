package kz.kaznaru.capstone.domain;

/** Placeholder entity. Replace with your domain model (no framework or I/O imports here). */
public record Item(long id, String title) {
    public Item {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }
}
