package com.example.paymentservice.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payment-transaction")
@Builder
public class PaymentTransaction {

    @Id
    private String id;

    @Field(value = "customerId")
    private String customerId;

    @Field(value = "paymentType")
    private String paymentType;

//    @Field(value = "providerTransactionId")
//    private String providerTransactionId;

    @Field(value = "amount")
    private Integer amount;

    @Field(value = "paymentFee")
    private Integer paymentFee;

    @Field(value = "status")
    private String status;

//    @Field(value = "provider")
//    private String provider;

    @Field(value = "createdAt")
    private String createdAt;

    @Field(value = "updatedAt")
    private String updatedAt;

}
