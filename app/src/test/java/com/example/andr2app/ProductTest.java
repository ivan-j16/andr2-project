package com.example.andr2app;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Random;

public class ProductTest {

    @Test
    public void testProductCreation(){
        String name = "Product test name";
        double price = 35.99;
        String url = "none";
        Random rand = new Random();
        int product_id = rand.nextInt(1000000);

        Product p = new Product(name, price, url, String.valueOf(product_id));

        assertEquals(p.getName(), name);
    }

    @Test
    public void testProductPriceFormatting(){
        String name = "Product test name";
        double price = 35.6;
        String url = "none";
        Random rand = new Random();
        int product_id = rand.nextInt(1000000);

        Product p = new Product(name, price, url, String.valueOf(product_id));

        assertEquals(p.getPriceFormatted(), "35.60 €");
    }
}
