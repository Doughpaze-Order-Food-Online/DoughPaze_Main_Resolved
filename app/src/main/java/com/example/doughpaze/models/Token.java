package com.example.doughpaze.models;

public class Token {
    private String token,orderId;

    public String getOrderId() {
        return orderId;
    }

    public String getToken() {
        return token;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
