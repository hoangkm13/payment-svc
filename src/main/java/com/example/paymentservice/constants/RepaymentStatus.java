package com.example.paymentservice.constants;

import org.springframework.lang.Nullable;

import java.util.Objects;

public enum RepaymentStatus {
    PAYMENT_SUCCESS("success", "Thành công"),
    PAYMENT_PENDING("pending", "Pending"),
    PAYMENT_PROCESSING("processing", "Trong tiến trình"),
    PAYMENT_FAILED("failed", "Thất bại"),
    PAYMENT_CANCEL("cancel", "Bị hủy"),
    PAYMENT_VERIFYING("verifying", "Đang xác thực");

    private final String value;
    private final String reasonPhrase;

    RepaymentStatus(String value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public String value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public String toString() {
        return this.value + " " + this.name();
    }

    @Nullable
    public static RepaymentStatus resolve(String repaymentStatusValue) {
        RepaymentStatus[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            RepaymentStatus repaymentStatus = var1[var3];
            if (Objects.equals(repaymentStatus.value, repaymentStatusValue)) {
                return repaymentStatus;
            }
        }

        return null;
    }

}