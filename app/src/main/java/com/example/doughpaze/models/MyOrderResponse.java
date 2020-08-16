package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyOrderResponse {

    @SerializedName("customer_name")
    String customer_name;

    @SerializedName("customer_phone")
    String customer_phone;

    @SerializedName("customer_email")
    String customer_email;

    @SerializedName("orderId")
    Double orderId;

    @SerializedName("Order_details")
    List<FoodCart> finalOrderList;

    @SerializedName("address")
    Address address;

    @SerializedName("TotalAmount")
    Double TotalAmount;

    @SerializedName("payment_mode")
    String payment_mode;


    public Double getOrderId() {
        return orderId;
    }

    public Address getAddress() {
        return address;
    }

    public Double getTotalAmount() {
        return TotalAmount;
    }

    public List<FoodCart> getFinalOrderList() {
        return finalOrderList;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setOrderId(Double orderId) {
        this.orderId = orderId;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public void setFinalOrderList(List<FoodCart> finalOrderList) {
        this.finalOrderList = finalOrderList;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public void setTotalAmount(Double totalAmount) {
        TotalAmount = totalAmount;
    }
}

