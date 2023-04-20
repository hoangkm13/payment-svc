package com.example.paymentservice.service.impl;

import com.example.paymentservice.constants.ErrorCode;
import com.example.paymentservice.constants.RepaymentStatus;
import com.example.paymentservice.controller.request.repayment.CreatePaymentRequest;
import com.example.paymentservice.controller.request.repayment.DeletePaymentRequest;
import com.example.paymentservice.controller.request.repayment.GetPaymentRequest;
import com.example.paymentservice.controller.request.repayment.UpdatePaymentRequest;
import com.example.paymentservice.entities.PaymentTransaction;
import com.example.paymentservice.exception.CustomException;
import com.example.paymentservice.repository.CustomerRepository;
import com.example.paymentservice.repository.PaymentTransactionRepository;
import com.example.paymentservice.service.CustomerService;
import com.example.paymentservice.service.RepaymentService;
import com.example.paymentservice.util.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
public class RepaymentServiceImpl implements RepaymentService {
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    public RepaymentServiceImpl(CustomerRepository customerRepository, CustomerService customerService, PaymentTransactionRepository paymentTransactionRepository) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
        this.paymentTransactionRepository = paymentTransactionRepository;

    }

    @Override
    public void checkAuthentication(String customerId) throws CustomException {
        String createdBy = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!customerId.equals(createdBy)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public PaymentTransaction createPaymentOrder(CreatePaymentRequest createPaymentRequest, int paymentFee) throws CustomException {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setId(UUID.randomUUID().toString());
        paymentTransaction.setCustomerId(createPaymentRequest.getCustomerId());
        paymentTransaction.setAmount(createPaymentRequest.getAmount() * paymentFee);
        paymentTransaction.setPaymentFee(paymentFee);
        paymentTransaction.setPaymentType(createPaymentRequest.getPaymentType());

        //Todo Gửi transaction sang bên thứ 3 lấy status rồi set vào status
        paymentTransaction.setStatus(RepaymentStatus.PAYMENT_SUCCESS.value());
        paymentTransaction.setCreatedAt(DateUtils.toDateString(new Date(), DateUtils.yyyyMMddTHHmmssSSS));
        paymentTransaction.setUpdatedAt(paymentTransaction.getCreatedAt());

        customerService.addPaymentTransaction(paymentTransaction);

        this.paymentTransactionRepository.save(paymentTransaction);

        return paymentTransaction;
    }

    @Override
    public PaymentTransaction updatePayment(UpdatePaymentRequest updatePaymentRequest, String paymentTransactionId) throws CustomException {
        var existedRepaymentTransaction = findRepaymentTransactionById(paymentTransactionId);

        existedRepaymentTransaction.setUpdatedAt(DateUtils.toDateString(new Date(), DateUtils.yyyyMMddTHHmmssSSS));
        existedRepaymentTransaction.setStatus(updatePaymentRequest.getRepaymentStatus().value());
        this.paymentTransactionRepository.save(existedRepaymentTransaction);
        return existedRepaymentTransaction;

    }

    @Override
    public PaymentTransaction deletePayment(DeletePaymentRequest deletePaymentRequest, String paymentTransactionId) throws CustomException {
        var customer = customerService.findById(deletePaymentRequest.getCustomerId());
        var existedRepaymentTransaction = findRepaymentTransactionById(paymentTransactionId);

        if (Objects.equals(existedRepaymentTransaction.getStatus(), RepaymentStatus.PAYMENT_PROCESSING.value())) {
            this.paymentTransactionRepository.delete(existedRepaymentTransaction);
        } else {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        customer.getListRepaymentTransactionIds().remove(existedRepaymentTransaction.getId());
        this.customerRepository.save(customer);

        return existedRepaymentTransaction;
    }

    @Override
    public PaymentTransaction findRepaymentTransactionById(String id) throws CustomException {
        var existedRepaymentTransaction = this.paymentTransactionRepository.findById(id);

        if (existedRepaymentTransaction.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_REPAYMENT_ORDER_ERROR);
        }

        return existedRepaymentTransaction.get();
    }

    @Override
    public List<PaymentTransaction> getAllPayments() throws CustomException {
        return this.paymentTransactionRepository.findAll();
    }
}
