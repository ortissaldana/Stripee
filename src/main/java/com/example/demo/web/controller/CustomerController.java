package com.example.demo.web.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stripe.Stripe;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin

@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private StripeService stripeService;

    @GetMapping("/{customerId}")
    public Map<String, Customer>  getCustomerAccount(@PathVariable String customerId) throws StripeException {
        try {
            Customer customer = stripeService.getCustomerAccount(customerId);

            Map<String, Customer> response = new HashMap<>();
            response.put("customerId", customer);
            return response;


        } finally {

        }
    }
}
