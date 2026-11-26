package kz.kaznaru.capstone.domain;

public class FixedDiscount implements DiscountStrategy {

    @Override
    public double calculateDiscount(double totalPrice, double value) {

        if (value > totalPrice) {
            throw new IllegalArgumentException(
                    "Скидка не может превышать стоимость корзины"
            );
        }

        return value;
    }
}
