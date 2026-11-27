
package kz.kaznaru.capstone.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentAdapter implements PaymentProcessor {

    private final LegacyPaymentSystem legacyPaymentSystem;

    public PaymentAdapter(
            LegacyPaymentSystem legacyPaymentSystem) {

        if (legacyPaymentSystem == null) {
            throw new IllegalArgumentException(
                    "Платёжная система не может быть null");
        }

        this.legacyPaymentSystem = legacyPaymentSystem;
    }

    @Override
    public boolean pay(double amount) {

        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                    "Некорректная сумма оплаты");
        }

        if (amount == 0) {
            return true;
        }

        long amountInTiyn;

        try {
            amountInTiyn = BigDecimal.valueOf(amount)
                    .movePointRight(2)
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();

        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "Сумма оплаты слишком большая", e);
        }

        if (amountInTiyn == 0) {
            throw new IllegalArgumentException(
                    "Сумма меньше минимальной единицы оплаты");
        }

        int result = legacyPaymentSystem.processPayment(
                amountInTiyn
        );

        return result == LegacyPaymentSystem.SUCCESS;
    }
}
