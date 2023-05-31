package com.example.demo.dto;

public class CreatePaymentResponse {
    public CreatePaymentResponse(String clientSecret) {
        this.clientSecret = clientSecret;
    }



    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    public String getClientSecret() {
        return clientSecret;
    }
    private String clientSecret;

}
