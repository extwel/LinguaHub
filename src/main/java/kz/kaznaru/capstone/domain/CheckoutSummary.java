package kz.kaznaru.capstone.domain;

public record CheckoutSummary(
        double totalPrice,
        double discountAmount,
        double certificateAmount,
        double finalPrice
) {
}
