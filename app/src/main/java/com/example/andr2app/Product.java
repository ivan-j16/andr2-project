package com.example.andr2app;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@SuppressWarnings("serial")
public class Product implements Serializable{
    private String name;
    private double price;
    private String photoUrl;
    private String id;
    private String userId;


    public Product() {
        //public constructor with no args needed for firestore
    }

    public Product(String name, double price, String photoUrl, String id, String userId) {
        this.name = name;
        this.price = price;
        this.photoUrl = photoUrl;
        this.id = id;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public String getPriceFormatted(){
        NumberFormat formatter = new DecimalFormat("#0.00");

        return formatter.format(price) + " €";
    }
}
