package kz.kaznaru.capstone.domain;

public interface DiscountStrategy {

    double calculateDiscount(double totalPrice, double value);
}
