package com.example.paymentservice.service;


import com.example.paymentservice.controller.request.auth.ResetPasswordDTO;
import com.example.paymentservice.controller.request.auth.UpdateCustomerDTO;
import com.example.paymentservice.controller.request.auth.CustomerDTO;
import com.example.paymentservice.entities.Customer;
import com.example.paymentservice.entities.PaymentTransaction;
import com.example.paymentservice.exception.CustomException;

public interface CustomerService {
    Customer findByUsername(String username) throws CustomException;

    Customer createUser(CustomerDTO customerDTO) throws CustomException;

    void checkPermission(String userId) throws CustomException;
    Customer findById(String customerId) throws CustomException;
    Customer preCheckUpdateUserInfo(UpdateCustomerDTO updateCustomerDTO, String currentUserId, String userId) throws CustomException;
    Customer resetPassword(ResetPasswordDTO resetPasswordDTO, String currentUserId, String userId) throws CustomException;
    void addPaymentTransaction(PaymentTransaction paymentTransaction) throws CustomException;
}
