package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;


import java.util.List;

public class subcategory_list {

    @SerializedName("_id")
    private String _id;

    @SerializedName("details")
    private List<Food_Details> details;


    public String getSubcategory() {
        return _id;
    }

    public void setSubcategory(String subcategory) {
        this._id = subcategory;
    }

    public List<Food_Details> getDetails() {
        return details;
    }

    public void setDetails(List<Food_Details> result) {
        details = result;
    }


}
