
package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.*;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CheckoutFacade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutFacadeTest {

    private Course createCourse() {

        return new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "English course", true
        );
    }

    @Test
    void checkoutSummaryWithoutDiscount() {

        CartService service = new CartService(new Cart());
        CheckoutFacade facade = new CheckoutFacade(service);

        service.addCourse(createCourse());

        CheckoutSummary summary = facade.prepareCheckout();

        assertEquals(25000.0, summary.totalPrice());
        assertEquals(0.0, summary.discountAmount());
        assertEquals(0.0, summary.certificateAmount());
        assertEquals(25000.0, summary.finalPrice());
    }

    @Test
    void checkoutSummaryWithDiscount() {

        CartService service = new CartService(new Cart());
        CheckoutFacade facade = new CheckoutFacade(service);

        service.addCourse(createCourse());

        service.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10)
        );

        CheckoutSummary summary = facade.prepareCheckout();

        assertEquals(2500.0, summary.discountAmount());
        assertEquals(22500.0, summary.finalPrice());
    }

    @Test
    void checkoutSummaryWithCertificate() {

        CartService service = new CartService(new Cart());
        CheckoutFacade facade = new CheckoutFacade(service);

        service.addCourse(createCourse());
        service.applyGiftCertificate("GIFT5000");

        CheckoutSummary summary = facade.prepareCheckout();

        assertEquals(5000.0, summary.certificateAmount());
        assertEquals(20000.0, summary.finalPrice());
    }

    @Test
    void checkoutWithDiscountAndCertificate() {

        CartService service = new CartService(new Cart());
        CheckoutFacade facade = new CheckoutFacade(service);

        service.addCourse(createCourse());

        service.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10)
        );

        service.applyGiftCertificate("GIFT5000");

        assertEquals(
                17500.0,
                facade.prepareCheckout().finalPrice()
        );

        assertEquals(17500.0, facade.checkout());
    }

    @Test
    void checkoutRegistersPurchasedCourse() {

        CartService service = new CartService(new Cart());
        CheckoutFacade facade = new CheckoutFacade(service);

        service.addCourse(createCourse());
        facade.checkout();

        assertTrue(service.isPurchased(1));
        assertTrue(service.getCourses().isEmpty());
    }

    @Test
    void emptyCartCannotBePrepared() {

        CartService service = new CartService(new Cart());
        CheckoutFacade facade = new CheckoutFacade(service);

        assertThrows(
                IllegalArgumentException.class,
                facade::prepareCheckout
        );
    }

    @Test
    void emptyCartCannotBePurchased() {

        CartService service = new CartService(new Cart());
        CheckoutFacade facade = new CheckoutFacade(service);

        assertThrows(
                IllegalArgumentException.class,
                facade::checkout
        );
    }

    @Test
    void checkoutNotifiesObservers() {

        CartService service = new CartService(new Cart());
        CheckoutFacade facade = new CheckoutFacade(service);

        service.addCourse(createCourse());
        facade.checkout();

        assertEquals(
                1, service.getEnrollmentNotifications().size()
        );

        assertEquals(
                1, service.getEnrollmentLogs().size()
        );
    }

    @Test
    void nullServiceRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutFacade(null)
        );
    }
}
