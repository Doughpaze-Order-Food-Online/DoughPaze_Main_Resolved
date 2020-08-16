package com.example.doughpaze.models;

import com.example.doughpaze.FoodList.PaymentDetails;

import java.util.List;

public class FinalOrder {
    private List<FoodCart> foodCarts;
    private Address address;
    private User user;
    private PaymentDetails paymentDetails;
    private Double total, orderId;
    private boolean coupon_applied;
    private String coupon_name;
    private Double discount;


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

    public Double getOrderId() {
        return orderId;
    }

    public void setOrderId(Double orderId) {
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


}
