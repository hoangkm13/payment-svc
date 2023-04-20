package com.example.paymentservice.service;


import com.example.paymentservice.controller.request.repayment.CreatePaymentRequest;
import com.example.paymentservice.controller.request.repayment.DeletePaymentRequest;
import com.example.paymentservice.controller.request.repayment.GetPaymentRequest;
import com.example.paymentservice.controller.request.repayment.UpdatePaymentRequest;
import com.example.paymentservice.entities.PaymentTransaction;
import com.example.paymentservice.exception.CustomException;

import java.util.List;

public interface RepaymentService {
    void checkAuthentication(String customerId) throws CustomException;

    PaymentTransaction createPaymentOrder(CreatePaymentRequest createPaymentRequest, int paymentFee) throws CustomException;
    PaymentTransaction updatePayment(UpdatePaymentRequest updatePaymentRequest, String paymentTransactionId) throws CustomException;
    PaymentTransaction deletePayment(DeletePaymentRequest deletePaymentRequest, String paymentTransactionId) throws CustomException;
    PaymentTransaction findRepaymentTransactionById(String id) throws CustomException;
    List<PaymentTransaction> getAllPayments() throws CustomException;
}
