package com.example.paymentservice.controller.request.repayment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotBlank
    private String customerId;

//    @NotBlank
//    private String applicationId;

    @NotBlank
    private String paymentType;

    @NotBlank
    private Integer amount;

}
