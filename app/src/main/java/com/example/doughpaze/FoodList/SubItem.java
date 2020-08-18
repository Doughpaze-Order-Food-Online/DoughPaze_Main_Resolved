package com.example.doughpaze.FoodList;

import java.util.Random;

public class SubItem {

    private String food_image,id;
    private String food_name;
    private int price;
    private int large_price;
    private String category, subcategory;


    public SubItem(String id,String food_image, String food_name, int price,String category,String subcategory) {
        this.food_image=food_image;
        this.food_name=food_name;
        this.price=price;
        this.category=category;
        this.subcategory=subcategory;
        this.id=id;

    }

    public SubItem(String id,String food_image, String food_name, int price,int large_price,String category,String subcategory) {
        this.food_image=food_image;
        this.food_name=food_name;
        this.price=price;
        this.large_price=large_price;
        this.category=category;
        this.subcategory=subcategory;
        this.id=id;

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

    public String getSubcategory() {
        return subcategory;
    }

    public String getCategory() {
        return category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
