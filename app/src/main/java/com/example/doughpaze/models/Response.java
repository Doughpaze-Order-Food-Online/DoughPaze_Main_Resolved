package com.example.doughpaze.models;

import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Response {

    private String message;
    private String token;
    private String email;
    private String dob;
    private String name;
    private String mobile_no;
    private String otp;
    private String type;
    private String result;
    private int position;
    private Boolean available;
    private Double saving;
    private String coupon_name;



    public String getMessage() {
        return message;
    }

    public String getOtp() {
        return otp;
    }

    public String getToken() {
        return token;
    }

    public String getemail(){return email ;}

    public String getname(){return name ;}

    public String getMobile_no(){return mobile_no ;}

    public String getDob(){return dob ;}

    public String getType(){return type;};

    public String getResult(){return result;}

    public int getPosition() {
        return position;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public Double getSaving() {
        return saving;
    }
}