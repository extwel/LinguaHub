package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscountStrategyTest {

    @Test
    void percentageDiscountWorks() {

        DiscountStrategy strategy = new PercentageDiscount();

        assertEquals(
                5000.0,
                strategy.calculateDiscount(50000, 10)
        );
    }

    @Test
    void fixedDiscountWorks() {

        DiscountStrategy strategy = new FixedDiscount();

        assertEquals(
                7000.0,
                strategy.calculateDiscount(50000, 7000)
        );
    }

    @Test
    void fullPercentageDiscountWorks() {

        Discount discount = new Discount(
                DiscountType.PERCENTAGE, 100
        );

        assertEquals(
                50000.0,
                discount.calculateDiscount(50000)
        );
    }

    @Test
    void excessiveFixedDiscountRejected() {

        DiscountStrategy strategy = new FixedDiscount();

        assertThrows(
                IllegalArgumentException.class,
                () -> strategy.calculateDiscount(5000, 10000)
        );
    }

    @Test
    void percentageStrategyIntegration() {

        Discount discount = new Discount(
                DiscountType.PERCENTAGE, 20
        );

        assertEquals(
                10000.0,
                discount.calculateDiscount(50000)
        );
    }

    @Test
    void fixedStrategyIntegration() {

        Discount discount = new Discount(
                DiscountType.FIXED, 5000
        );

        assertEquals(
                5000.0,
                discount.calculateDiscount(50000)
        );
    }
}
