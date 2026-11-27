
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
import kz.kaznaru.capstone.domain.PaymentAdapter;
import kz.kaznaru.capstone.domain.LegacyPaymentSystem;
import kz.kaznaru.capstone.service.CartService;

class GiftCertificateTest {

    private CartService cartService;

    private Course createFirstCourse() {

        return new LanguageCourse(
                1, "English A1", "English", "Beginner",
                25000, 40, "English course", true
        );
    }

    private Course createSecondCourse() {

        return new LanguageCourse(
                2, "English B2", "English", "Upper-Intermediate",
                45000, 60, "English course", true
        );
    }

    @BeforeEach
    void setUp() {

        cartService = new CartService(new Cart());

        cartService.addCourse(createFirstCourse());
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

        assertEquals(25000.0,
                cartService.getFinalPrice());
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

        assertEquals(20000.0,
                cartService.getFinalPrice());
    }

    @Test
    void shouldNotAllowNegativeFinalPrice() {

        cartService.applyGiftCertificate("GIFT50000");

        assertEquals(0.0,
                cartService.getFinalPrice());

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

        assertEquals(0.0,
                cartService.getFinalPrice());
    }

    @Test
    void shouldRecalculatePriceAfterDiscountRemoval() {

        cartService.applyDiscount(
                new Discount(DiscountType.FIXED, 5000));

        cartService.applyGiftCertificate("GIFT5000");

        assertEquals(15000.0,
                cartService.getFinalPrice());

        cartService.removeDiscount();

        assertEquals(20000.0,
                cartService.getFinalPrice());
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

        assertEquals(0.0,
                cartService.getDiscountAmount());

        assertEquals(0.0,
                cartService.getFinalPrice());
    }

    // FIX-01: новые регрессионные тесты

    @Test
    void shouldNotConsumeCertificateBeforePurchase() {

        cartService.applyGiftCertificate("GIFT5000");

        // Удаляем курс до покупки
        cartService.removeCourse(1);

        // Добавляем курс заново
        cartService.addCourse(createFirstCourse());

        // Сертификат должен оставаться доступным
        assertDoesNotThrow(
                () -> cartService.applyGiftCertificate("GIFT5000")
        );

        assertEquals(20000.0,
                cartService.getFinalPrice());
    }

    @Test
    void shouldNotConsumeCertificateAfterCheckoutPreview() {

        cartService.applyGiftCertificate("GIFT5000");

        // Предварительное подтверждение заказа
        cartService.prepareCheckout();

        // Пользователь не подтверждает покупку
        assertFalse(cartService.isPurchased(1));

        cartService.removeCourse(1);

        cartService.addCourse(createFirstCourse());

        assertDoesNotThrow(
                () -> cartService.applyGiftCertificate("GIFT5000")
        );

        assertEquals(5000.0,
                cartService.getGiftCertificateAmount());
    }

    @Test
    void shouldConsumeCertificateAfterSuccessfulPurchase() {

        cartService.applyGiftCertificate("GIFT5000");

        assertEquals(20000.0,
                cartService.purchaseCourses());

        assertTrue(cartService.isPurchased(1));

        cartService.addCourse(createSecondCourse());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> cartService.applyGiftCertificate("GIFT5000")
                );

        assertEquals(
                "Сертификат уже использован",
                exception.getMessage()
        );
    }

    @Test
    void shouldNotConsumeCertificateAfterFailedPayment() {

        CartService failingService = new CartService(
                new Cart(),
                new PaymentAdapter(
                        new LegacyPaymentSystem(false)
                )
        );

        failingService.addCourse(createFirstCourse());

        failingService.applyGiftCertificate("GIFT5000");

        assertThrows(
                IllegalArgumentException.class,
                failingService::purchaseCourses
        );

        assertFalse(failingService.isPurchased(1));

        assertEquals(1,
                failingService.getCourses().size());

        assertEquals(5000.0,
                failingService.getGiftCertificateAmount());

        // Отменяем неуспешный заказ
        failingService.removeCourse(1);

        failingService.addCourse(createFirstCourse());

        // Сертификат не должен быть списан
        assertDoesNotThrow(
                () -> failingService.applyGiftCertificate("GIFT5000")
        );
    }

    @Test
    void shouldConsumeCertificateForZeroPricePurchase() {

        cartService.applyGiftCertificate("GIFT50000");

        assertEquals(0.0,
                cartService.purchaseCourses());

        assertTrue(cartService.isPurchased(1));

        cartService.addCourse(createSecondCourse());

        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.applyGiftCertificate("GIFT50000")
        );
    }

    @Test
    void shouldNotConsumeCertificateWhenDiscountCoversOrder() {

        cartService.applyGiftCertificate("GIFT5000");

        cartService.applyDiscount(
                new Discount(
                        DiscountType.PERCENTAGE,
                        100
                )
        );

        assertEquals(0.0,
                cartService.getGiftCertificateAmount());

        assertEquals(0.0,
                cartService.purchaseCourses());

        assertTrue(cartService.isPurchased(1));

        cartService.addCourse(createSecondCourse());

        // Сертификат не участвовал в оплате,
        // поэтому его можно использовать снова
        assertDoesNotThrow(
                () -> cartService.applyGiftCertificate("GIFT5000")
        );

        assertEquals(40000.0,
                cartService.getFinalPrice());
    }

    @Test
    void shouldNotConsumeCertificateAfterEmptyCartPurchase() {

        cartService.applyGiftCertificate("GIFT5000");

        cartService.removeCourse(1);

        assertThrows(
                IllegalArgumentException.class,
                cartService::purchaseCourses
        );

        cartService.addCourse(createFirstCourse());

        assertDoesNotThrow(
                () -> cartService.applyGiftCertificate("GIFT5000")
        );
    }
}
