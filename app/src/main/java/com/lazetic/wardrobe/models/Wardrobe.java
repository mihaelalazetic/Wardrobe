package com.lazetic.wardrobe.models;

import android.graphics.Bitmap;

public class Wardrobe {
    public String name;
    public String color;
    public WardrobeCategory category;
    public Bitmap image; // TODO
    public int id;

    public Wardrobe(String name, String color, WardrobeCategory category, Bitmap image, int id) {
        this.name = name;
        this.color = color;
        this.category = category;
        this.image = image;
        this.id = id;
    }

    public Wardrobe(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public WardrobeCategory getCategory() {
        return category;
    }

    public void setCategory(WardrobeCategory category) {
        this.category = category;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
