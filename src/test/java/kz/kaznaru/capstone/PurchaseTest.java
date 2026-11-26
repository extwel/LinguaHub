package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Discount;
import kz.kaznaru.capstone.domain.DiscountType;
import kz.kaznaru.capstone.domain.LanguageCourse;
import kz.kaznaru.capstone.service.CartService;

class PurchaseTest {

    private CartService cartService;
    private Course firstCourse;
    private Course secondCourse;

    @BeforeEach
    void setUp() {

        cartService = new CartService(new Cart());

        firstCourse = new LanguageCourse(
                1, "English A1", "English", "Beginner",
                25000, 40, "English course", true
        );

        secondCourse = new LanguageCourse(
                2, "English B2", "English", "Upper-Intermediate",
                45000, 60, "English course", true
        );
    }

    @Test
    void shouldPurchaseCourse() {

        cartService.addCourse(firstCourse);

        double paid = cartService.purchaseCourses();

        assertEquals(25000.0, paid);
        assertTrue(cartService.isPurchased(1));
        assertEquals(1,
                cartService.getPurchasedCourses().size());
    }

    @Test
    void shouldPurchaseMultipleCourses() {

        cartService.addCourse(firstCourse);
        cartService.addCourse(secondCourse);

        assertEquals(70000.0,
                cartService.purchaseCourses());

        assertEquals(2,
                cartService.getPurchasedCourses().size());

        assertTrue(cartService.isPurchased(1));
        assertTrue(cartService.isPurchased(2));
    }

    @Test
    void shouldClearCartAfterPurchase() {

        cartService.addCourse(firstCourse);

        cartService.purchaseCourses();

        assertTrue(cartService.getCourses().isEmpty());
        assertEquals(0.0, cartService.getTotalPrice());
    }

    @Test
    void shouldRejectEmptyCart() {

        assertThrows(IllegalArgumentException.class,
                cartService::purchaseCourses);

        assertTrue(
                cartService.getPurchasedCourses().isEmpty());
    }

    @Test
    void shouldRejectAlreadyPurchasedCourse() {

        cartService.addCourse(firstCourse);
        cartService.purchaseCourses();

        assertThrows(IllegalArgumentException.class,
                () -> cartService.addCourse(firstCourse));
    }

    @Test
    void shouldPurchaseWithDiscountAndCertificate() {

        cartService.addCourse(firstCourse);

        cartService.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10));

        cartService.applyGiftCertificate("GIFT5000");

        assertEquals(17500.0,
                cartService.purchaseCourses());

        assertTrue(cartService.isPurchased(1));
    }

    @Test
    void shouldResetDiscountAfterPurchase() {

        cartService.addCourse(firstCourse);

        cartService.applyDiscount(
                new Discount(DiscountType.FIXED, 5000));

        cartService.purchaseCourses();

        assertNull(cartService.getDiscount());
        assertEquals(0.0,
                cartService.getDiscountAmount());
    }

    @Test
    void shouldResetCertificateAfterPurchase() {

        cartService.addCourse(firstCourse);
        cartService.applyGiftCertificate("GIFT5000");

        cartService.purchaseCourses();

        cartService.addCourse(secondCourse);

        assertEquals(0.0,
                cartService.getGiftCertificateAmount());

        assertEquals(45000.0,
                cartService.getFinalPrice());

        assertThrows(IllegalArgumentException.class,
                () -> cartService.applyGiftCertificate("GIFT5000"));
    }

    @Test
    void shouldKeepPreviouslyPurchasedCourses() {

        cartService.addCourse(firstCourse);
        cartService.purchaseCourses();

        cartService.addCourse(secondCourse);
        cartService.purchaseCourses();

        assertEquals(2,
                cartService.getPurchasedCourses().size());
    }

    @Test
    void shouldAllowZeroPricePurchase() {

        cartService.addCourse(firstCourse);

        cartService.applyGiftCertificate("GIFT50000");

        assertEquals(0.0,
                cartService.purchaseCourses());

        assertTrue(cartService.isPurchased(1));
        assertTrue(cartService.getCourses().isEmpty());
    }

    @Test
    void shouldProtectPurchasedCoursesList() {

        cartService.addCourse(firstCourse);
        cartService.purchaseCourses();

        assertThrows(UnsupportedOperationException.class,
                () -> cartService.getPurchasedCourses().clear());
    }

    @Test
    void shouldRejectInvalidCoursePrice() {

        Course invalidCourse = new LanguageCourse(
                3, "Invalid course", "English", "Beginner",
                Double.NaN, 40, "Test course", true
        );

        cartService.addCourse(invalidCourse);

        assertThrows(IllegalArgumentException.class,
                cartService::purchaseCourses);

        assertTrue(cartService.getPurchasedCourses().isEmpty());
        assertEquals(1, cartService.getCourses().size());
    }
}
