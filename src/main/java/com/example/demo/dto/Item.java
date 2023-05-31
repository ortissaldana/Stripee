package com.example.demo.dto;


import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("items")
    Object[] items;
    private String id;
    private int price;

    public Item(String id, int price) {
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }


    Object[] itemss = new Object[] {
            new Item("xl-tshirt", 20),
            new Item("hat", 10),
            new Item("pants", 30)
    };



}

// ...

