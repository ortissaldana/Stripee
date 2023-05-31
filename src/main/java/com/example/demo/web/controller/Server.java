package com.example.demo.web.controller;

import com.stripe.exception.StripeException;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.stripe.Stripe;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@SpringBootApplication
@RestController
@CrossOrigin
@ConfigurationProperties(prefix = "stripe")
public class Server {

    @Value("${stripe.PUBLISHABLE_KEY}")
    private String publishableKey;
    @Value("${stripe.SECRET_KEY}")
    private String SECRET_KEY;

    @PostMapping(value = "/create-checkout-session", produces = "application/json")
    public CreateCheckoutSessionResponse createCheckoutSession(@RequestBody CreateCheckoutSessionRequest request) throws StripeException {
        Stripe.apiKey = SECRET_KEY;

        String YOUR_DOMAIN = "http://localhost:5252";

        Product product = createProduct(request.getProductName());

        Long priceAmount = request.getPriceAmount();
        if (priceAmount == null) {
            throw new IllegalArgumentException("priceAmount cannot be null");
        }
        long convertedAmount = convertToSmallestUnit(priceAmount);

        Price price = createPrice(convertedAmount, request.getCurrency(), product.getId());

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(price.getCurrency())
                        .setProduct(product.getId())
                        .setUnitAmount(price.getUnitAmount())
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT).addPaymentMethodType(SessionCreateParams.PaymentMethodType.PAYPAL)
                        .setSuccessUrl(YOUR_DOMAIN + "?success=true")
                        .setCancelUrl(YOUR_DOMAIN + "?canceled=true")
                        .addLineItem(lineItem)
                        .build();

        Session session = Session.create(params);

        CreateCheckoutSessionResponse response = new CreateCheckoutSessionResponse();
        response.setUrl(session.getUrl());

        return response;
    }

    private Product createProduct(String productName) {
        try {
            ProductCreateParams productParams = ProductCreateParams.builder()
                    .setName(productName)
                    .build();
            return Product.create(productParams);
        } catch (Exception e) {
            // Manejar cualquier error de creación del producto
            throw new RuntimeException("Error al crear el producto en Stripe", e);
        }
    }

    private Price createPrice(Long priceAmount, String currency, String productId) {
        try {
            if (priceAmount == null) {
                throw new IllegalArgumentException("priceAmount cannot be null");
            }

            long amount = priceAmount.longValue();

            PriceCreateParams priceParams = PriceCreateParams.builder()
                    .setUnitAmount(amount)
                    .setCurrency(currency)
                    .setProduct(productId)
                    .build();
            return Price.create(priceParams);
        } catch (Exception e) {
            throw new RuntimeException("Error creating price in Stripe", e);
        }
    }


    private Long convertToSmallestUnit(Long priceAmount) {
        // Assuming the priceAmount is in the base currency unit (e.g., dollars)
        // Convert it to the smallest currency unit (e.g., cents)
        return priceAmount * 100;
    }
}

class CreateCheckoutSessionRequest {
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(Long priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private String productName;
    private Long priceAmount;
    private String currency;

    // Getter and setter methods for productName, priceAmount, and currency

    // Define any other necessary fields for the request body
}

class CreateCheckoutSessionResponse {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Getter and setter methods for url

    // Define any other necessary fields for the response
}
