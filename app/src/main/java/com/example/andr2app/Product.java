package com.example.andr2app;

import java.io.Serializable;

public class Product {
    private String name;
    private double price;
    private String photoUrl;

    public Product() {
        //public constructor with no args needed for firestore
    }

    public Product(String name, double price, String photoUrl) {
        this.name = name;
        this.price = price;
        this.photoUrl = photoUrl;
    }

    public String getName(){
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
