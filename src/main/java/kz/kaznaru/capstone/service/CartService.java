
package kz.kaznaru.capstone.service;

import java.util.List;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;

public class CartService {

    private final Cart cart;

    public CartService(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Корзина не может быть пустой");
        }

        this.cart = cart;
    }

    public boolean addCourse(Course course) {
        return cart.addCourse(course);
    }

    public List<Course> getCourses() {
        return cart.getCourses();
    }

    public double getTotalPrice() {
        return cart.getTotalPrice();
    }
}
