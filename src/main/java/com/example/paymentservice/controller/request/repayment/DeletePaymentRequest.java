package com.example.paymentservice.controller.request.repayment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeletePaymentRequest {
    @NotBlank
    private String customerId;
}
