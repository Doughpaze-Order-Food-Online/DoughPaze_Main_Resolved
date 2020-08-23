package com.example.doughpaze.models;

import java.util.Date;
import java.util.List;

public class FinalOrder {
    private List<FoodCart> foodCarts;
    private Address address;
    private User user;
    private PaymentDetails paymentDetails;
    private Double total,tax;
    private boolean coupon_applied;
    private String coupon_name, orderId;
    private Double discount;
    private Date date;
    private int delivery,itemtotal;


    public Address getAddress() {
        return address;
    }

    public List<FoodCart> getFoodCarts() {
        return foodCarts;
    }

    public User getUser() {
        return user;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setFoodCarts(List<FoodCart> foodCarts) {
        this.foodCarts = foodCarts;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getTotal() {
        return total;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public void setCoupon_applied(boolean coupon_applied) {
        this.coupon_applied = coupon_applied;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public void setTax(Double tax) {
        this.tax = tax;
    }

    public void setItemtotal(int itemtotal) {
        this.itemtotal = itemtotal;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }
}
