
package kz.kaznaru.capstone.domain;

public class Discount {

    private final DiscountType type;
    private final double value;

    public Discount(DiscountType type, double value) {
        if (type == null) {
            throw new IllegalArgumentException("Тип скидки не может быть пустым");
        }

        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException("Размер скидки должен быть положительным числом");
        }

        if (type == DiscountType.PERCENTAGE && value > 100) {
            throw new IllegalArgumentException("Процент скидки не может превышать 100%");
        }

        this.type = type;
        this.value = value;
    }

    public DiscountType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public double calculateDiscount(double totalPrice) {
        if (!Double.isFinite(totalPrice) || totalPrice < 0) {
            throw new IllegalArgumentException("Стоимость не может быть отрицательной");
        }

        if (type == DiscountType.PERCENTAGE) {
            return totalPrice * value / 100;
        }

        if (value > totalPrice) {
            throw new IllegalArgumentException(
                    "Скидка не может превышать стоимость корзины"
            );
        }

        return value;
    }
}
