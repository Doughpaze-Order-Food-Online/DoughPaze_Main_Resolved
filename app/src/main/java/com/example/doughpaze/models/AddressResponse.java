package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressResponse {

    @SerializedName("Address")
    private List<Address> address;

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }
}
