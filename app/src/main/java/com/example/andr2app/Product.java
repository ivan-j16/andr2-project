package com.example.andr2app;

import java.io.Serializable;

public class Product {
    private String name;
    private double price;
    private String photoUrl;

    private String id;

    public Product() {
        //public constructor with no args needed for firestore
    }

    public Product(String name, double price, String photoUrl, String id) {
        this.name = name;
        this.price = price;
        this.photoUrl = photoUrl;
        this.id = id;
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

    public String getId() {
        return id;
    }
}
