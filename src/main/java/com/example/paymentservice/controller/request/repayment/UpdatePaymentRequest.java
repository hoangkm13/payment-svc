package com.example.paymentservice.controller.request.repayment;

import com.example.paymentservice.constants.RepaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentRequest {

    @NotBlank
    private String customerId;

    @NotBlank
    private RepaymentStatus repaymentStatus;

}
