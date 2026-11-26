
package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.LanguageCourse;

class CartTest {

    @Test
    void shouldAddCourseToCart() {
        Cart cart = new Cart();

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        assertTrue(cart.addCourse(course));
        assertEquals(1, cart.getCourses().size());
    }

    @Test
    void shouldNotAddDuplicateCourse() {
        Cart cart = new Cart();

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        assertTrue(cart.addCourse(course));
        assertFalse(cart.addCourse(course));

        assertEquals(1, cart.getCourses().size());
    }

    @Test
    void shouldCalculateTotalPrice() {
        Cart cart = new Cart();

        Course firstCourse = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", true
        );

        Course secondCourse = new LanguageCourse(
                2, "English B2", "English", "B2",
                35000, 60, "Intermediate English course", true
        );

        cart.addCourse(firstCourse);
        cart.addCourse(secondCourse);

        assertEquals(60000.0, cart.getTotalPrice());
    }

    @Test
    void shouldNotAddUnavailableCourse() {
        Cart cart = new Cart();

        Course course = new LanguageCourse(
                1, "English A1", "English", "A1",
                25000, 40, "Beginner English course", false
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> cart.addCourse(course)
        );

        assertEquals(0, cart.getCourses().size());
    }
}
