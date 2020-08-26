package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Coupon implements Serializable {

    @SerializedName("coupon_name")
    private String coupon_name;

    @SerializedName("category")
    private String category;

    @SerializedName("discount")
    private int discount;

    @SerializedName("max_discount")
    private int max_discount;

    @SerializedName("min_amount")
    private int min_amount;

    @SerializedName("limit")
     private int limit;

    @SerializedName("coupon_location")
    String coupon_location;

    @SerializedName("expiry")
    Date expiry;


    public String getCategory() {
        return category;
    }

    public int getDiscount() {
        return discount;
    }

    public int getMax_discount() {
        return max_discount;
    }

    public int getMin_amount() {
        return min_amount;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setMin_amount(int min_amount) {
        this.min_amount = min_amount;
    }

    public void setMax_discount(int max_discount) {
        this.max_discount = max_discount;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public String getCoupon_location() {
        return coupon_location;
    }

    public void setCoupon_location(String coupon_location) {
        this.coupon_location = coupon_location;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }
}
