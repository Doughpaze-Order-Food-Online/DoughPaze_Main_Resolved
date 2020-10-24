package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class MyOrderResponse {

    @SerializedName("_id")
    String _id;

    @SerializedName("customer_name")
    String customer_name;

    @SerializedName("customer_phone")
    String customer_phone;

    @SerializedName("customer_email")
    String customer_email;

    @SerializedName("orderId")
    String orderId;

    @SerializedName("Order_details")
    List<FoodCart> finalOrderList;

    @SerializedName("address")
    Address address;

    @SerializedName("TotalAmount")
    Double TotalAmount;

    @SerializedName("payment_mode")
    String payment_mode;

    @SerializedName("date")
    Date date;

    @SerializedName("order_status")
    String order_status;

    @SerializedName("payment_details")
    PaymentDetails paymentDetails;

    @SerializedName("coupon_applied")
    Boolean coupon_applied;

    @SerializedName("coupon_name")
    String coupon_name;

    @SerializedName("payment_status")
    Boolean payment_status;


    public String getOrderId() {
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

    public void setOrderId(String orderId) {
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

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public Boolean getCoupon_applied() {
        return coupon_applied;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_applied(Boolean coupon_applied) {
        this.coupon_applied = coupon_applied;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public Boolean getPayment_status() {
        return payment_status;
    }
}



