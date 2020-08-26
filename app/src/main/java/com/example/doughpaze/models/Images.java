package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Images implements Serializable {

    @SerializedName("banners")
    private List<banners> bannersList;

    @SerializedName("coupon")
    private List<Coupon> CouponsList;

    public void setBannersList(List<banners> bannersList) {
        this.bannersList = bannersList;
    }

    public void setCouponsList(List<Coupon> couponsList) {
        CouponsList = couponsList;
    }

    public List<banners> getBannersList() {
        return bannersList;
    }

    public List<Coupon> getCouponsList() {
        return CouponsList;
    }
}
