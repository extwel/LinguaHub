package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.CheckoutSummary;

public class CheckoutFacade {

    private final CartService cartService;

    public CheckoutFacade(CartService cartService) {

        if (cartService == null) {
            throw new IllegalArgumentException(
                    "CartService не может быть null"
            );
        }

        this.cartService = cartService;
    }

    public CheckoutSummary prepareCheckout() {

        if (cartService.getCourses().isEmpty()) {
            throw new IllegalArgumentException(
                    "Корзина пуста"
            );
        }

        return new CheckoutSummary(
                cartService.getTotalPrice(),
                cartService.getDiscountAmount(),
                cartService.getGiftCertificateAmount(),
                cartService.getFinalPrice()
        );
    }

    public double checkout() {
        return cartService.purchaseCourses();
    }
}
