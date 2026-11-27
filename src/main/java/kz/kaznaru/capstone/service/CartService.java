
package kz.kaznaru.capstone.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;

import kz.kaznaru.capstone.domain.*;

public class CartService {

    private final Cart cart;
    private Discount discount;

    private final Map<String, GiftCertificate> certificates =
            new HashMap<>();

    private GiftCertificate appliedCertificate;

    private final List<Course> purchasedCourses =
            new ArrayList<>();

    // Observer
    private final Enrollment enrollment =
            new Enrollment();

    private final EnrollmentNotification notification =
            new EnrollmentNotification();

    private final EnrollmentLogger logger =
            new EnrollmentLogger();

    public CartService(Cart cart) {

        if (cart == null) {
            throw new IllegalArgumentException(
                    "Корзина не может быть пустой");
        }

        this.cart = cart;

        enrollment.addObserver(notification);
        enrollment.addObserver(logger);

        certificates.put("GIFT5000",
                new GiftCertificate("GIFT5000", 5000));

        certificates.put("GIFT10000",
                new GiftCertificate("GIFT10000", 10000));

        certificates.put("GIFT50000",
                new GiftCertificate("GIFT50000", 50000));

        GiftCertificate usedCertificate =
                new GiftCertificate("USED1000", 1000);

        usedCertificate.use();

        certificates.put("USED1000", usedCertificate);
    }

    public boolean addCourse(Course course) {

        if (course != null && isPurchased(course.getId())) {
            throw new IllegalArgumentException(
                    "Вы уже купили этот курс");
        }

        return cart.addCourse(course);
    }

    public boolean removeCourse(long courseId) {
        return cart.removeCourse(courseId);
    }

    public void applyDiscount(Discount discount) {

        if (discount == null) {
            throw new IllegalArgumentException(
                    "Скидка не может быть пустой");
        }

        discount.calculateDiscount(cart.getTotalPrice());
        this.discount = discount;
    }

    public void removeDiscount() {
        this.discount = null;
    }

    public Discount getDiscount() {
        return discount;
    }

    public double getDiscountAmount() {

        if (discount == null) {
            return 0.0;
        }

        if (discount.getType() == DiscountType.FIXED) {
            return Math.min(
                    discount.getValue(),
                    cart.getTotalPrice()
            );
        }

        return discount.calculateDiscount(cart.getTotalPrice());
    }

    public double getDiscountedTotalPrice() {

        return Math.max(
                0,
                cart.getTotalPrice() - getDiscountAmount()
        );
    }

    public void applyGiftCertificate(String code) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Введите код сертификата");
        }

        GiftCertificate certificate = certificates.get(
                code.trim().toUpperCase(Locale.ROOT)
        );

        if (certificate == null) {
            throw new IllegalArgumentException(
                    "Недействительный сертификат");
        }

        if (certificate.isUsed()) {
            throw new IllegalArgumentException(
                    "Сертификат уже использован");
        }

        if (cart.getCourses().isEmpty()) {
            throw new IllegalArgumentException(
                    "Корзина пуста");
        }

        if (appliedCertificate != null) {
            throw new IllegalArgumentException(
                    "Сертификат уже применён к заказу");
        }

        if (getDiscountedTotalPrice() <= 0) {
            throw new IllegalArgumentException(
                    "Заказ уже не требует оплаты");
        }

        certificate.use();
        appliedCertificate = certificate;
    }

    public double getGiftCertificateAmount() {

        if (appliedCertificate == null) {
            return 0.0;
        }

        return Math.min(
                appliedCertificate.getAmount(),
                getDiscountedTotalPrice()
        );
    }

    public double getFinalPrice() {

        return Math.max(
                0,
                getDiscountedTotalPrice()
                        - getGiftCertificateAmount()
        );
    }

    public boolean isPurchased(long courseId) {

        return purchasedCourses.stream()
                .anyMatch(course -> course.getId() == courseId);
    }

    public List<Course> getPurchasedCourses() {
        return List.copyOf(purchasedCourses);
    }

    public double purchaseCourses() {

        if (cart.getCourses().isEmpty()) {
            throw new IllegalArgumentException(
                    "Нельзя купить курсы: корзина пуста");
        }

        for (Course course : cart.getCourses()) {

            if (!course.isAvailable()) {
                throw new IllegalArgumentException(
                        "Курс недоступен для покупки");
            }

            if (isPurchased(course.getId())) {
                throw new IllegalArgumentException(
                        "Курс уже куплен");
            }

            if (!Double.isFinite(course.getPrice())
                    || course.getPrice() < 0) {

                throw new IllegalArgumentException(
                        "Некорректная стоимость курса");
            }
        }

        double finalPrice = getFinalPrice();

        if (!Double.isFinite(finalPrice) || finalPrice < 0) {
            throw new IllegalArgumentException(
                    "Некорректная стоимость заказа");
        }

        List<Course> coursesToPurchase =
                new ArrayList<>(cart.getCourses());

        purchasedCourses.addAll(coursesToPurchase);

        for (Course course : coursesToPurchase) {
            cart.removeCourse(course.getId());
        }

        discount = null;
        appliedCertificate = null;

        // Observer: уведомление после успешной покупки
        for (Course course : coursesToPurchase) {
            enrollment.notifyObservers(course);
        }

        return finalPrice;
    }

    // Observer: получение уведомлений
    public List<String> getEnrollmentNotifications() {
        return notification.getNotifications();
    }

    // Observer: получение журнала
    public List<String> getEnrollmentLogs() {
        return logger.getLogs();
    }

    public List<Course> getCourses() {
        return cart.getCourses();
    }

    public double getTotalPrice() {
        return cart.getTotalPrice();
    }
}
