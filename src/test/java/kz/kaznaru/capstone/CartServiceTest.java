
package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Discount;
import kz.kaznaru.capstone.domain.DiscountType;
import kz.kaznaru.capstone.domain.LanguageCourse;
import kz.kaznaru.capstone.service.CartService;

class CartServiceTest {

    @Test
    void shouldAddCourseThroughService() {
        CartService cartService = new CartService(new Cart());

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        assertTrue(cartService.addCourse(course));
        assertEquals(1, cartService.getCourses().size());
    }

    @Test
    void shouldReturnTotalPriceThroughService() {
        CartService cartService = new CartService(new Cart());

        Course firstCourse = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        Course secondCourse = new LanguageCourse(
                2, "English B2", "English", "B2",
                35000, 60, "Intermediate English course", true
        );

        cartService.addCourse(firstCourse);
        cartService.addCourse(secondCourse);

        assertEquals(60000.0, cartService.getTotalPrice());
    }

    @Test
    void shouldRejectNullCart() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CartService(null)
        );
    }

    // US-04: Удаление курса через сервис
    @Test
    void shouldRemoveCourseThroughService() {
        CartService cartService = new CartService(new Cart());

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        cartService.addCourse(course);

        assertTrue(cartService.removeCourse(1));
        assertTrue(cartService.getCourses().isEmpty());
        assertEquals(0.0, cartService.getTotalPrice());
    }

    // US-04: Проверка отсутствующего курса
    @Test
    void shouldReturnFalseWhenCourseDoesNotExist() {
        CartService cartService = new CartService(new Cart());

        assertFalse(cartService.removeCourse(99));
    }

    // US-07: Проверка итоговой стоимости со скидкой
    @Test
    void shouldCalculateFinalPriceWithDiscount() {
        CartService cartService = new CartService(new Cart());

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        cartService.addCourse(course);

        cartService.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10)
        );

        assertEquals(25000.0, cartService.getTotalPrice());
        assertEquals(2500.0, cartService.getDiscountAmount());
        assertEquals(22500.0, cartService.getFinalPrice());
    }

    // US-07: Проверка скидки и подарочного сертификата одновременно
    @Test
    void shouldCalculateFinalPriceWithDiscountAndGiftCertificate() {
        CartService cartService = new CartService(new Cart());

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        cartService.addCourse(course);

        cartService.applyDiscount(
                new Discount(DiscountType.PERCENTAGE, 10)
        );

        cartService.applyGiftCertificate("GIFT5000");

        assertEquals(25000.0, cartService.getTotalPrice());
        assertEquals(2500.0, cartService.getDiscountAmount());
        assertEquals(5000.0, cartService.getGiftCertificateAmount());
        assertEquals(17500.0, cartService.getFinalPrice());
    }

    // US-07: Итоговая стоимость не может быть отрицательной
    @Test
    void shouldNotAllowNegativeFinalPrice() {
        CartService cartService = new CartService(new Cart());

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        cartService.addCourse(course);
        cartService.applyGiftCertificate("GIFT50000");

        assertEquals(25000.0, cartService.getGiftCertificateAmount());
        assertEquals(0.0, cartService.getFinalPrice());
    }
}
