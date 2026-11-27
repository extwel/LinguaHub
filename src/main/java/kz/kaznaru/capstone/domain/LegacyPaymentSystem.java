
package kz.kaznaru.capstone.domain;

public class LegacyPaymentSystem {

    public static final int SUCCESS = 0;
    public static final int DECLINED = 1;
    public static final int INVALID_AMOUNT = 2;

    private final boolean paymentAvailable;

    public LegacyPaymentSystem() {
        this(true);
    }

    public LegacyPaymentSystem(boolean paymentAvailable) {
        this.paymentAvailable = paymentAvailable;
    }

    public int processPayment(long amountInTiyn) {

        if (amountInTiyn <= 0) {
            return INVALID_AMOUNT;
        }

        if (!paymentAvailable) {
            return DECLINED;
        }

        return SUCCESS;
    }
}
