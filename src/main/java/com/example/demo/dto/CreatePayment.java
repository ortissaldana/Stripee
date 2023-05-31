package com.example.demo.dto;


public class CreatePayment {




    public CreatePayment(String id, int price) {
        this.id = id;
        this.amount = price;
    }



    private int amount;

    private String id;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getFeatureRequest() {
        return id;
    }

    public void setFeatureRequest(String featureRequest) {
        this.id = featureRequest;
    }
    public Object[] getItems() {
        return items;
    }


    public void Item(String id, int price) {
        this.id = id;
        this.amount = price;
    }

    public String getId() {
        return id;
    }

    public int getPrice() {
        return amount;
    }



    Object[] items=new Object[] {
            new CreatePayment("xl-tshirt", 20),
            new CreatePayment("hat", 10),
            new CreatePayment("pants", 30)
    };

}
