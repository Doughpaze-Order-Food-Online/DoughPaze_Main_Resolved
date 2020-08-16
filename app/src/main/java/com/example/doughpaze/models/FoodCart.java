package com.example.doughpaze.models;

import java.util.Random;

public class FoodCart {
    private String food_name;
    private String food_category;
    private String food_subcategory;
    private int price, alt_price;
    private int quantity;
    private String size;
    double id;

    public FoodCart(){
        this.quantity=0;
    }

    public String getFood_name()
    {
        return food_name;
    }

    public void setFood_name(String name)
    {
        this.food_name=name;
    }

    public String getFood_category()
    {
        return  food_category;
    }

    public void setFood_Category(String category)
    {
        this.food_category=category;
    }

    public String getFood_subcategory() {
        return food_subcategory;
    }

    public void setFood_subcategory(String food_subcategory) {
        this.food_subcategory = food_subcategory;
    }

    public int getPrice() {
        return price;
    }


    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increment()
    {
        this.quantity++;
    }

    public void decrement()
    {
        this.quantity--;
    }

    public String  getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getAlt_price() {
        return alt_price;
    }

    public void setAlt_price(int alt_price) {
        this.alt_price = alt_price;
    }

    public double getId() {
        return id;
    }

    public void setId(String id) {
        double rand;
        Random random=new Random();
        rand=random.nextDouble();
        this.id=rand;
    }
}
