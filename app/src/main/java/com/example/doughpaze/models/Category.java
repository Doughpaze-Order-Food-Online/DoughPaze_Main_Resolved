package com.example.doughpaze.models;

import android.graphics.drawable.Drawable;

public class Category {
    private String name;
    private int drawable;

    public Category(String name, int drawable)
    {
        this.name=name;
        this.drawable=drawable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getDrawable() {
        return drawable;
    }
}
