package com.example.doughpaze.models;

import com.google.gson.annotations.SerializedName;

public class Food_Details {


    @SerializedName("_id")
    private String _id;

    @SerializedName("food_image")
    private String food_image;

    @SerializedName("food_name")
    private String food_name;

    @SerializedName("category")
    private String category;

    @SerializedName("subcategory")
    private String subcategory;

    @SerializedName("price")
    private int price;

    @SerializedName("large_price")
    private int large_price;

    @SerializedName("description")
    private String description;

    public String getID() {
        return _id;
    }

    public void setID(String id) {
        this._id = id;
    }

    public String getFood_image() {
        return food_image;
    }

    public void setFood_image(String food_image) {
        this.food_image = food_image;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category=category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String  getSubcategory() {
        return subcategory;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price=price;
    }


    public int getLarge_price() {
        return large_price;
    }

    public void setLarge_price(int large_price) {
        this.large_price = large_price;
    }

    public String get_id() {
        return _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
