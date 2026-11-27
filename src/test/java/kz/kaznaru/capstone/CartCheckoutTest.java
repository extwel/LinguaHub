
package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.*;
import kz.kaznaru.capstone.service.CartService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartCheckoutTest {

    private Course createCourse() {

        return new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "English course", true
        );
    }

    @Test
    void checkoutSummaryWithoutDiscount() {

        CartService service = new CartService(new Cart());

        service.addCourse(createCourse());

        CheckoutSummary summary = service.prepareCheckout();

        assertEquals(25000.0, summary.totalPrice());
        assertEquals(0.0, summary.discountAmount());
        assertEquals(0.0, summary.certificateAmount());
        assertEquals(25000.0, summary.finalPrice());
    }

    @Test
    void checkoutSummaryWithDiscount() {

        CartService service = new CartService(new Cart());

        service.addCourse(createCourse());

        service.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10)
        );

        CheckoutSummary summary = service.prepareCheckout();

        assertEquals(2500.0, summary.discountAmount());
        assertEquals(22500.0, summary.finalPrice());
    }

    @Test
    void checkoutSummaryWithCertificate() {

        CartService service = new CartService(new Cart());

        service.addCourse(createCourse());
        service.applyGiftCertificate("GIFT5000");

        CheckoutSummary summary = service.prepareCheckout();

        assertEquals(5000.0, summary.certificateAmount());
        assertEquals(20000.0, summary.finalPrice());
    }

    @Test
    void checkoutWithDiscountAndCertificate() {

        CartService service = new CartService(new Cart());

        service.addCourse(createCourse());

        service.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10)
        );

        service.applyGiftCertificate("GIFT5000");

        assertEquals(
                17500.0,
                service.prepareCheckout().finalPrice()
        );

        assertEquals(17500.0, service.purchaseCourses());
    }

    @Test
    void checkoutRegistersPurchasedCourse() {

        CartService service = new CartService(new Cart());

        service.addCourse(createCourse());
        service.purchaseCourses();

        assertTrue(service.isPurchased(1));
        assertTrue(service.getCourses().isEmpty());
    }

    @Test
    void emptyCartCannotBePrepared() {

        CartService service = new CartService(new Cart());

        assertThrows(
                IllegalArgumentException.class,
                service::prepareCheckout
        );
    }

    @Test
    void emptyCartCannotBePurchased() {

        CartService service = new CartService(new Cart());

        assertThrows(
                IllegalArgumentException.class,
                service::purchaseCourses
        );
    }

    @Test
    void checkoutNotifiesObservers() {

        CartService service = new CartService(new Cart());

        service.addCourse(createCourse());
        service.purchaseCourses();

        assertEquals(
                1, service.getEnrollmentNotifications().size()
        );

        assertEquals(
                1, service.getEnrollmentLogs().size()
        );
    }

    @Test
    void nullCartRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CartService(null)
        );
    }
}
