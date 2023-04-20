package com.example.paymentservice.enums;

import org.springframework.lang.Nullable;

import java.util.Objects;

public enum PaymentType {
    FIRST_STAGE("FIRST_STAGE", "Số tiền chuyển mức độ 1"),
    SECOND_STAGE("SECOND_STAGE", "Số tiền chuyển mức độ 2"),
    THIRD_STAGE("THIRD_STAGE", "Số tiền chuyển mức độ 3");

    private final String value;
    private final String reasonPhrase;

    PaymentType(String value, String reasonPhrase) {
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
    public static PaymentType resolve(String applicationTypeName) {
        PaymentType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            PaymentType paymentType = var1[var3];
            if (Objects.equals(paymentType.value, applicationTypeName)) {
                return paymentType;
            }
        }

        return null;
    }

}