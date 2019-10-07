package com.safwat.abanoub.nsrcompany;

public class Product {
    public String pushID;
    public String title;
    public String price;
    public String image;

    public Product() {
    }

    public Product(String title, String price) {
        this.title = title;
        this.price = price;
    }

    public Product(String title, String price, String image) {
        this.title = title;
        this.price = price;
        this.image = image;
    }
}
