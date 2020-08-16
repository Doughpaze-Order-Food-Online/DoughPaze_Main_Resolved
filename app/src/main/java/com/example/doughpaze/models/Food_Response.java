package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;


import java.util.List;

public class Food_Response {

    @SerializedName("food")
    private List<subcategory_list> food;

    public List<subcategory_list> getResult() {
        return food;
    }

    public void setResult(List<subcategory_list> result) {
        food = result;
    }
}


