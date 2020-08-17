package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

public class couponsAvailable {
    @SerializedName("coupon_name")
    private String coupon_name;

    @SerializedName("limit")
    private int limit;
}
