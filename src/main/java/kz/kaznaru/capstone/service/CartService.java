
package kz.kaznaru.capstone.service;

import java.util.List;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Discount;

public class CartService {

    private final Cart cart;
    private Discount discount;

    public CartService(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Корзина не может быть пустой");
        }

        this.cart = cart;
    }

    public boolean addCourse(Course course) {
        return cart.addCourse(course);
    }

    // US-04: Удаление курса через сервис
    public boolean removeCourse(long courseId) {
        return cart.removeCourse(courseId);
    }

    // US-05: Применение скидки
    public void applyDiscount(Discount discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Скидка не может быть пустой");
        }

        discount.calculateDiscount(cart.getTotalPrice());
        this.discount = discount;
    }

    // US-05: Отмена скидки
    public void removeDiscount() {
        this.discount = null;
    }

    public Discount getDiscount() {
        return discount;
    }

    // US-05: Получение размера скидки
    public double getDiscountAmount() {
        if (discount == null) {
            return 0.0;
        }

        return discount.calculateDiscount(cart.getTotalPrice());
    }

    // US-05: Итоговая стоимость со скидкой
    public double getDiscountedTotalPrice() {
        return cart.getTotalPrice() - getDiscountAmount();
    }

    public List<Course> getCourses() {
        return cart.getCourses();
    }

    public double getTotalPrice() {
        return cart.getTotalPrice();
    }
}
