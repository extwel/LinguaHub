package kz.kaznaru.capstone.domain;

public class Discount {

    private final DiscountType type;
    private final double value;
    private final DiscountStrategy strategy;

    public Discount(DiscountType type, double value) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Тип скидки не может быть пустым"
            );
        }

        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException(
                    "Размер скидки должен быть положительным числом"
            );
        }

        if (type == DiscountType.PERCENTAGE && value > 100) {
            throw new IllegalArgumentException(
                    "Процент скидки не может превышать 100%"
            );
        }

        this.type = type;
        this.value = value;

        this.strategy = switch (type) {
            case PERCENTAGE -> new PercentageDiscount();
            case FIXED -> new FixedDiscount();
        };
    }

    public DiscountType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public double calculateDiscount(double totalPrice) {

        if (!Double.isFinite(totalPrice) || totalPrice < 0) {
            throw new IllegalArgumentException(
                    "Стоимость не может быть отрицательной"
            );
        }

        return strategy.calculateDiscount(totalPrice, value);
    }
}
