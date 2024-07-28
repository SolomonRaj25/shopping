package com.online.shopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderID;
    //@Column(name = "Customer Name")
    private String fullName;
    //@Column(name = "Contact Number")
    private String contact;
    //@Column(name = "Delivery Address")
    private String deliveryTo;
    //@Column(name = "Product")
    private String product;
    //@Column(name = "ProductID")
    private Long productId;
    //@Column(name = "Payment Type")
    private String paymentType;
    //@Column(name = "Total Charges")
    private String paidAmount;
    //@Column(name = "Ordered Date")
    private String orderedDate;
    //@Column(name = "Expected Delivery")
    private String expectedDelivery;

    public void setOrderedDate(String orderedDate) {
        this.orderedDate = String.valueOf(new Date(System.currentTimeMillis()));
    }

    public void setExpectedDelivery(String expectedDelivery) {
        this.expectedDelivery = String.valueOf(new Date(System.currentTimeMillis()));
    }

}
