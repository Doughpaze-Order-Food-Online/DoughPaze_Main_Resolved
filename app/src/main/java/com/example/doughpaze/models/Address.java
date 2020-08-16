package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Address {
    String house_details, address,type,mobile_no,_id;
    double latitude,longitude;


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getHouse_details() {
        return house_details;
    }

    public String getType() {
        return type;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setHouse_details(String house_details) {
        this.house_details = house_details;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public class address{
        @SerializedName("address")
        private AddressResponse addressResponse;

        public AddressResponse getAddress() {
            return addressResponse;
        }

        public void setAddress(AddressResponse addressResponse) {
            this.addressResponse = addressResponse;
        }
    }
}
