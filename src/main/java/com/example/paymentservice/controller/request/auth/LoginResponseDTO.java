package com.example.paymentservice.controller.request.auth;


import com.example.paymentservice.entities.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    @NotNull
    private String token;

    private Customer customer;
}
