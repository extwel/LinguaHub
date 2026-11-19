package kz.kaznaru.capstone.domain;

public abstract class Course {
    private final long id;
    private final String title;
    private final String language;
    private final String level;
    private final double price;
    private final int durationHours;
    private final String description;
    private final boolean available;

    public Course(
            long id,
            String title,
            String language,
            String level,
            double price,
            int durationHours,
            String description,
            boolean available
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название курса не должно быть пустым");
        }

        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException("Язык курса не должен быть пустым");
        }

        if (level == null || level.isBlank()) {
            throw new IllegalArgumentException("Уровень курса не должен быть пустым");
        }

        if (price < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }

        if (durationHours <= 0) {
            throw new IllegalArgumentException("Длительность курса должна быть больше нуля");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание курса не должно быть пустым");
        }

        this.id = id;
        this.title = title;
        this.language = language;
        this.level = level;
        this.price = price;
        this.durationHours = durationHours;
        this.description = description;
        this.available = available;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getLevel() {
        return level;
    }

    public double getPrice() {
        return price;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }
}
