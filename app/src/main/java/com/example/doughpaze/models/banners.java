package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class banners implements Serializable {

    @SerializedName("banner_location")
    String banner_location;

    @SerializedName("banner_name")
    String banner_name;

    public void setBanner_location(String banner_location) {
        this.banner_location = banner_location;
    }

    public void setBanner_name(String banner_name) {
        this.banner_name = banner_name;
    }

    public String getBanner_location() {
        return banner_location;
    }

    public String getBanner_name() {
        return banner_name;
    }
}
