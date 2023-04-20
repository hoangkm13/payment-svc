package com.example.paymentservice.repository;

import com.example.paymentservice.entities.PaymentTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String> {
}
