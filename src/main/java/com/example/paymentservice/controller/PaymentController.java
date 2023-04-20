package com.example.paymentservice.controller;

import com.example.paymentservice.constants.ErrorCode;
import com.example.paymentservice.controller.request.repayment.CreatePaymentRequest;
import com.example.paymentservice.controller.request.repayment.DeletePaymentRequest;
import com.example.paymentservice.controller.request.repayment.GetPaymentRequest;
import com.example.paymentservice.controller.request.repayment.UpdatePaymentRequest;
import com.example.paymentservice.entities.PaymentTransaction;
import com.example.paymentservice.enums.PaymentType;
import com.example.paymentservice.exception.CustomException;
import com.example.paymentservice.model.ApiResponse;
import com.example.paymentservice.service.RepaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/paymentTransaction")
public class PaymentController {
    private final RepaymentService repaymentService;

    @Autowired
    public PaymentController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    @PostMapping(produces = (MediaType.APPLICATION_JSON_VALUE))
    public ApiResponse<PaymentTransaction> createPaymentOrder(@Valid @RequestBody CreatePaymentRequest request) throws Exception {
        this.repaymentService.checkAuthentication(request.getCustomerId());

        PaymentTransaction paymentTransaction;
        int paymentFee;
        switch (PaymentType.valueOf(request.getPaymentType())) {
            case FIRST_STAGE:
                paymentFee = 1;
                if (request.getAmount() > 5000) {
                    throw new CustomException(ErrorCode.AMOUNT_INVALID);
                }
                break;
            case SECOND_STAGE:
                paymentFee = 2;
                if (request.getAmount() > 10000) {
                    throw new CustomException(ErrorCode.AMOUNT_INVALID);
                }
                break;
            case THIRD_STAGE:
                paymentFee = 3;
                if (request.getAmount() > 15000) {
                    throw new CustomException(ErrorCode.AMOUNT_INVALID);
                }
                break;
            default:
                throw new CustomException(ErrorCode.WRONG_APPLICATION_TYPE);
        }
        request.setPaymentType(PaymentType.valueOf(request.getPaymentType()).value());
        paymentTransaction = repaymentService.createPaymentOrder(request, paymentFee);

        return ApiResponse.successWithResult(paymentTransaction);
    }

    @PutMapping(value = "{paymentTransactionId}", produces = (MediaType.APPLICATION_JSON_VALUE))
    public ApiResponse<PaymentTransaction> updatePayment(@PathVariable String paymentTransactionId, @Valid @RequestBody UpdatePaymentRequest request) throws Exception {
        this.repaymentService.checkAuthentication(request.getCustomerId());
        var updatedPayment = this.repaymentService.updatePayment(request, paymentTransactionId);

        return ApiResponse.successWithResult(updatedPayment);
    }

    @DeleteMapping(value = "{paymentTransactionId}", produces = (MediaType.APPLICATION_JSON_VALUE))
    public ApiResponse<PaymentTransaction> deletePayment(@PathVariable String paymentTransactionId, @Valid @RequestBody DeletePaymentRequest request) throws Exception {
        this.repaymentService.checkAuthentication(request.getCustomerId());
        var deletedPayment = this.repaymentService.deletePayment(request, paymentTransactionId);

        return ApiResponse.successWithResult(deletedPayment);
    }

    @GetMapping(value = "{paymentTransactionId}", produces = (MediaType.APPLICATION_JSON_VALUE))
    public ApiResponse<PaymentTransaction> getPayment(@PathVariable String paymentTransactionId, @Valid @RequestBody GetPaymentRequest request) throws Exception {
        this.repaymentService.checkAuthentication(request.getCustomerId());
        var existedPayment = this.repaymentService.findRepaymentTransactionById(paymentTransactionId);

        return ApiResponse.successWithResult(existedPayment);
    }

    @GetMapping(value = "getAllPayment", produces = (MediaType.APPLICATION_JSON_VALUE))
    public ApiResponse<List<PaymentTransaction>> getAllPayment(@Valid @RequestBody GetPaymentRequest request) throws Exception {
        this.repaymentService.checkAuthentication(request.getCustomerId());
        var allPayments = this.repaymentService.getAllPayments();

        return ApiResponse.successWithResult(allPayments);
    }
}
