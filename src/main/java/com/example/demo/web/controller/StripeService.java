package com.example.demo.web.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@Service
@CrossOrigin

public class StripeService {

    @Value("${stripe.SECRET_KEY}")
    private String stripeSecretKey;

    public Customer getCustomerAccount(String customerId) throws StripeException {
        Stripe.apiKey = "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";

        return Customer.retrieve(customerId);
    }
}
