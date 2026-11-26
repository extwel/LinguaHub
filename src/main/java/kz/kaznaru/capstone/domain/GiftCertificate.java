package kz.kaznaru.capstone.domain;

public class GiftCertificate {

    private final String code;
    private final double amount;
    private boolean used;

    public GiftCertificate(String code, double amount) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Код не может быть пустым");
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Некорректный номинал");
        }

        this.code = code;
        this.amount = amount;
        this.used = false;
    }

    public String getCode() {
        return code;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isUsed() {
        return used;
    }

    public void use() {
        if (used) {
            throw new IllegalArgumentException(
                    "Сертификат уже использован");
        }

        used = true;
    }
}
