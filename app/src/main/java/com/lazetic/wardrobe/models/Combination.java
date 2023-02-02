package com.lazetic.wardrobe.models;

import android.graphics.Bitmap;

public class Combination {
    public String name;
    public Bitmap top;
    public Bitmap bottom;
    public Bitmap shoe;
    public Bitmap accessory;
    public String date;
    public int id;

    public Combination(String name, Bitmap top, Bitmap bottom, Bitmap shoe, Bitmap accessory, String date) {
        this.name = name;
        this.top = top;
        this.bottom = bottom;
        this.shoe = shoe;
        this.accessory = accessory;
        this.date = date;
    }

    public Combination(Bitmap top, Bitmap bottom, Bitmap shoe, Bitmap accessory, String date) {
        this.top = top;
        this.bottom = bottom;
        this.shoe = shoe;
        this.accessory = accessory;
        this.date = date;
    }

    public Combination(String name, Bitmap top, Bitmap bottom, Bitmap shoe, Bitmap accessory, String date, int id) {
        this.name = name;
        this.top = top;
        this.bottom = bottom;
        this.shoe = shoe;
        this.accessory = accessory;
        this.date = date;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getTop() {
        return top;
    }

    public void setTop(Bitmap top) {
        this.top = top;
    }

    public Bitmap getBottom() {
        return bottom;
    }

    public void setBottom(Bitmap bottom) {
        this.bottom = bottom;
    }

    public Bitmap getShoe() {
        return shoe;
    }

    public void setShoe(Bitmap shoe) {
        this.shoe = shoe;
    }

    public Bitmap getAccessory() {
        return accessory;
    }

    public void setAccessory(Bitmap accessory) {
        this.accessory = accessory;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
