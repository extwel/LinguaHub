
package kz.kaznaru.capstone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import kz.kaznaru.capstone.domain.*;
import kz.kaznaru.capstone.service.CartService;

class PaymentAdapterTest {

    private Course createCourse() {

        return new LanguageCourse(
                1,
                "English A1",
                "English",
                "A1",
                25000,
                40,
                "English course",
                true
        );
    }

    @Test
    void shouldProcessSuccessfulPayment() {

        PaymentProcessor processor =
                new PaymentAdapter(
                        new LegacyPaymentSystem()
                );

        assertTrue(processor.pay(25000));
    }

    @Test
    void shouldRejectDeclinedPayment() {

        PaymentProcessor processor =
                new PaymentAdapter(
                        new LegacyPaymentSystem(false)
                );

        assertFalse(processor.pay(25000));
    }

    @Test
    void shouldConvertTengeToTiyn() {

        LegacyPaymentSystem system =
                new LegacyPaymentSystem() {

                    @Override
                    public int processPayment(
                            long amountInTiyn) {

                        assertEquals(
                                1750000L,
                                amountInTiyn
                        );

                        return SUCCESS;
                    }
                };

        PaymentProcessor processor =
                new PaymentAdapter(system);

        assertTrue(processor.pay(17500));
    }

    @Test
    void shouldConvertFractionalAmount() {

        LegacyPaymentSystem system =
                new LegacyPaymentSystem() {

                    @Override
                    public int processPayment(
                            long amountInTiyn) {

                        assertEquals(
                                123456L,
                                amountInTiyn
                        );

                        return SUCCESS;
                    }
                };

        PaymentProcessor processor =
                new PaymentAdapter(system);

        assertTrue(processor.pay(1234.56));
    }

    @Test
    void shouldRoundFractionalTiyn() {

        LegacyPaymentSystem system =
                new LegacyPaymentSystem() {

                    @Override
                    public int processPayment(
                            long amountInTiyn) {

                        assertEquals(
                                101L,
                                amountInTiyn
                        );

                        return SUCCESS;
                    }
                };

        PaymentProcessor processor =
                new PaymentAdapter(system);

        assertTrue(processor.pay(1.005));
    }

    @Test
    void shouldRejectNegativeAmount() {

        PaymentProcessor processor =
                new PaymentAdapter(
                        new LegacyPaymentSystem()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.pay(-100)
        );
    }

    @Test
    void shouldRejectInvalidAmounts() {

        PaymentProcessor processor =
                new PaymentAdapter(
                        new LegacyPaymentSystem()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.pay(Double.NaN)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.pay(
                        Double.POSITIVE_INFINITY)
        );
    }

    @Test
    void shouldRejectOverflowAmount() {

        PaymentProcessor processor =
                new PaymentAdapter(
                        new LegacyPaymentSystem()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.pay(Double.MAX_VALUE)
        );
    }

    @Test
    void shouldRejectAmountBelowMinimumUnit() {

        PaymentProcessor processor =
                new PaymentAdapter(
                        new LegacyPaymentSystem()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.pay(0.001)
        );
    }

    @Test
    void shouldAllowZeroAmount() {

        PaymentProcessor processor =
                new PaymentAdapter(
                        new LegacyPaymentSystem(false)
                );

        assertTrue(processor.pay(0));
    }

    @Test
    void shouldRejectNullPaymentSystem() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new PaymentAdapter(null)
        );
    }

    @Test
    void shouldRejectNullPaymentProcessor() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CartService(
                        new Cart(),
                        null
                )
        );
    }

    @Test
    void shouldPurchaseCourseThroughAdapter() {

        CartService service = new CartService(
                new Cart(),
                new PaymentAdapter(
                        new LegacyPaymentSystem()
                )
        );

        service.addCourse(createCourse());

        assertEquals(
                25000.0,
                service.purchaseCourses()
        );

        assertTrue(service.isPurchased(1));
        assertTrue(service.getCourses().isEmpty());

        assertEquals(
                1,
                service.getEnrollmentNotifications().size()
        );

        assertEquals(
                1,
                service.getEnrollmentLogs().size()
        );
    }

    @Test
    void shouldNotPurchaseWhenPaymentDeclined() {

        CartService service = new CartService(
                new Cart(),
                new PaymentAdapter(
                        new LegacyPaymentSystem(false)
                )
        );

        service.addCourse(createCourse());

        assertThrows(
                IllegalArgumentException.class,
                service::purchaseCourses
        );

        assertFalse(service.isPurchased(1));

        assertEquals(
                1,
                service.getCourses().size()
        );

        assertTrue(
                service.getPurchasedCourses().isEmpty()
        );

        assertTrue(
                service.getEnrollmentNotifications().isEmpty()
        );

        assertTrue(
                service.getEnrollmentLogs().isEmpty()
        );
    }

    @Test
    void shouldPayFinalPriceWithDiscountAndCertificate() {

        LegacyPaymentSystem system =
                new LegacyPaymentSystem() {

                    @Override
                    public int processPayment(
                            long amountInTiyn) {

                        assertEquals(
                                1750000L,
                                amountInTiyn
                        );

                        return SUCCESS;
                    }
                };

        CartService service = new CartService(
                new Cart(),
                new PaymentAdapter(system)
        );

        service.addCourse(createCourse());

        service.applyDiscount(
                new Discount(
                        DiscountType.PERCENTAGE,
                        10
                )
        );

        service.applyGiftCertificate("GIFT5000");

        assertEquals(
                17500.0,
                service.purchaseCourses()
        );

        assertTrue(service.isPurchased(1));
        assertTrue(service.getCourses().isEmpty());
    }

    @Test
    void shouldKeepDiscountAndCertificateAfterFailedPayment() {

        CartService service = new CartService(
                new Cart(),
                new PaymentAdapter(
                        new LegacyPaymentSystem(false)
                )
        );

        service.addCourse(createCourse());

        service.applyDiscount(
                new Discount(
                        DiscountType.PERCENTAGE,
                        10
                )
        );

        service.applyGiftCertificate("GIFT5000");

        assertThrows(
                IllegalArgumentException.class,
                service::purchaseCourses
        );

        assertFalse(service.isPurchased(1));

        assertEquals(
                1,
                service.getCourses().size()
        );

        assertEquals(
                2500.0,
                service.getDiscountAmount()
        );

        assertEquals(
                5000.0,
                service.getGiftCertificateAmount()
        );

        assertEquals(
                17500.0,
                service.getFinalPrice()
        );
    }

    @Test
    void shouldAllowFreePurchaseWithoutPayment() {

        PaymentProcessor processor = amount -> {
            fail("Платёж не должен выполняться");
            return false;
        };

        CartService service = new CartService(
                new Cart(),
                processor
        );

        service.addCourse(createCourse());

        service.applyGiftCertificate("GIFT50000");

        assertEquals(
                0.0,
                service.purchaseCourses()
        );

        assertTrue(service.isPurchased(1));
        assertTrue(service.getCourses().isEmpty());
    }

    @Test
    void shouldRejectInvalidLegacyPaymentAmount() {

        LegacyPaymentSystem system =
                new LegacyPaymentSystem();

        assertEquals(
                LegacyPaymentSystem.INVALID_AMOUNT,
                system.processPayment(-100)
        );

        assertEquals(
                LegacyPaymentSystem.INVALID_AMOUNT,
                system.processPayment(0)
        );
    }
}
