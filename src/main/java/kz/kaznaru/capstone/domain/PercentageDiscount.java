package kz.kaznaru.capstone.domain;

public class PercentageDiscount implements DiscountStrategy {

    @Override
    public double calculateDiscount(double totalPrice, double value) {
        return totalPrice * value / 100;
    }
}
