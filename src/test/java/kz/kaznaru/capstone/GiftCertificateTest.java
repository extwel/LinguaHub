package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Discount;
import kz.kaznaru.capstone.domain.DiscountType;
import kz.kaznaru.capstone.domain.GiftCertificate;
import kz.kaznaru.capstone.domain.LanguageCourse;
import kz.kaznaru.capstone.service.CartService;

class GiftCertificateTest {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(new Cart());

        Course course = new LanguageCourse(
                1, "English A1", "English", "Beginner",
                25000, 40, "English course", true
        );

        cartService.addCourse(course);
    }

    @Test
    void shouldCreateCertificate() {
        GiftCertificate certificate =
                new GiftCertificate("TEST1000", 1000);

        assertEquals("TEST1000", certificate.getCode());
        assertEquals(1000.0, certificate.getAmount());
        assertFalse(certificate.isUsed());
    }

    @Test
    void shouldRejectInvalidCertificateData() {
        assertThrows(IllegalArgumentException.class,
                () -> new GiftCertificate("", 1000));

        assertThrows(IllegalArgumentException.class,
                () -> new GiftCertificate("TEST", -1000));
    }

    @Test
    void shouldMarkCertificateAsUsed() {
        GiftCertificate certificate =
                new GiftCertificate("TEST1000", 1000);

        certificate.use();

        assertTrue(certificate.isUsed());

        assertThrows(IllegalArgumentException.class,
                certificate::use);
    }

    @Test
    void shouldApplyValidCertificate() {
        cartService.applyGiftCertificate("GIFT5000");

        assertEquals(5000.0,
                cartService.getGiftCertificateAmount());

        assertEquals(20000.0,
                cartService.getFinalPrice());
    }

    @Test
    void shouldRejectInvalidCertificate() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.applyGiftCertificate("INVALID"));

        assertEquals(25000.0, cartService.getFinalPrice());
    }

    @Test
    void shouldRejectUsedCertificate() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.applyGiftCertificate("USED1000"));
    }

    @Test
    void shouldNotApplyCertificateTwice() {
        cartService.applyGiftCertificate("GIFT5000");

        assertThrows(IllegalArgumentException.class,
                () -> cartService.applyGiftCertificate("GIFT5000"));

        assertEquals(20000.0, cartService.getFinalPrice());
    }

    @Test
    void shouldNotAllowNegativeFinalPrice() {
        cartService.applyGiftCertificate("GIFT50000");

        assertEquals(0.0, cartService.getFinalPrice());
        assertEquals(25000.0,
                cartService.getGiftCertificateAmount());
    }

    @Test
    void shouldWorkTogetherWithDiscount() {
        cartService.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10));

        cartService.applyGiftCertificate("GIFT5000");

        assertEquals(2500.0,
                cartService.getDiscountAmount());

        assertEquals(17500.0,
                cartService.getFinalPrice());
    }

    @Test
    void shouldRejectCertificateForEmptyCart() {
        CartService emptyCartService =
                new CartService(new Cart());

        assertThrows(IllegalArgumentException.class,
                () -> emptyCartService.applyGiftCertificate("GIFT5000"));
    }

    @Test
    void shouldRejectEmptyCode() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.applyGiftCertificate(""));

        assertThrows(IllegalArgumentException.class,
                () -> cartService.applyGiftCertificate(null));
    }

    @Test
    void shouldRecalculatePriceAfterCourseRemoval() {
        cartService.applyGiftCertificate("GIFT5000");

        cartService.removeCourse(1);

        assertEquals(0.0, cartService.getFinalPrice());
    }

    @Test
    void shouldRecalculatePriceAfterDiscountRemoval() {
        cartService.applyDiscount(
                new Discount(DiscountType.FIXED, 5000));

        cartService.applyGiftCertificate("GIFT5000");

        assertEquals(15000.0, cartService.getFinalPrice());

        cartService.removeDiscount();

        assertEquals(20000.0, cartService.getFinalPrice());
    }

    @Test
    void shouldRejectSecondCertificate() {
        cartService.applyGiftCertificate("GIFT5000");

        assertThrows(IllegalArgumentException.class,
                () -> cartService.applyGiftCertificate("GIFT10000"));
    }

    @Test
    void shouldLimitFixedDiscountAfterCourseRemoval() {
        cartService.applyDiscount(
                new Discount(DiscountType.FIXED, 5000));

        cartService.removeCourse(1);

        assertEquals(0.0, cartService.getDiscountAmount());
        assertEquals(0.0, cartService.getFinalPrice());
    }
}
