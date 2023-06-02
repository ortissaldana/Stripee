package com.example.demo.web.controller;
import com.example.demo.dto.CreatePayment;
import com.example.demo.dto.CreatePaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@RestController
@CrossOrigin
public class PaymentController {

    @Value("${stripe.PUBLISHABLE_KEY}")
    private String publishableKey;
    @Value("${stripe.SECRET_KEY}")
    private String SECRET_KEY;

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePayment request) {
        try {
            Stripe.apiKey = SECRET_KEY; // Reemplaza con tu Stripe Secret Key

            Map<String, Object> bankTransfer = new HashMap<String, Object>();

            bankTransfer.put("type", "mx_bank_transfer");

            Map<String, Object> customerBalance = new HashMap<String, Object>();
            customerBalance.put("funding_type", "bank_transfer");
            customerBalance.put("bank_transfer", bankTransfer);

            PaymentIntentCreateParams.PaymentMethodOptions pmo =
                    PaymentIntentCreateParams.PaymentMethodOptions.builder()
                            .putExtraParam("customer_balance", customerBalance)
                            .build();



            RequestOptions requestOptions = RequestOptions.builder().setApiKey(SECRET_KEY).build();

            Customer customer = Customer.create((Map<String, Object>) null, requestOptions);




            List<String> metodos = new ArrayList<>();
            metodos.add("customer_balance");

            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()

                    .setCurrency("mxn") // Reemplaza con la moneda deseada
                    .setAmount(1000L) // Reemplaza con el monto deseado
                    .setDescription("uuwu")
                    .setCustomer("cus_NrqZuXNiuXF4Ph")
                    .addAllPaymentMethodType(metodos)
                    .setPaymentMethodData(PaymentIntentCreateParams.PaymentMethodData.builder()
                            .putExtraParam("type", "customer_balance")
                            .build())
                    .setPaymentMethodOptions(pmo)
                    .build();



//
//              .setPaymentMethod("pm_1N6AmPAW1QMD0rARguKU7ehI")
////                    .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
//                    .setAutomaticPaymentMethods(
//                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//                                    .setEnabled(true)
//                                    .build()
//                    )




            PaymentIntent paymentIntent = PaymentIntent.create(createParams);
            return new CreatePaymentResponse(paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/create-payment-intent2")
    public CreatePaymentResponse createPaymentIntent2(@RequestParam("paymentMethodId") String paymentMethodId) {
        try {
            Stripe.apiKey = SECRET_KEY; // Reemplaza con tu Stripe Secret Key

            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setCurrency("MXN")
                    .setAmount(1000L) // Reemplaza con el monto deseado en centavos
                    .setCustomer("cus_NrqZuXNiuXF4Ph") // Reemplaza con el ID del cliente
                    .setPaymentMethod("pm_1NEC9lAW1QMD0rAREbajGSqe") // Reemplaza con el ID del método de pago
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(createParams);
            paymentIntent.confirm();
            return new CreatePaymentResponse(paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }






    @GetMapping("/customers")
    public Map<String, List<Charge>> getAllCustomers() {
        String secretKey = "your_stripe_secret_key";

        Stripe.apiKey = SECRET_KEY;

        Map<String, List<Charge>> customerTransactions = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("limit", 100); // Set the number of customers to retrieve per page

        try {
            CustomerCollection customers = Customer.list(params);

            // Process the customers object and extract the customer data as needed
            for (Customer customer : customers.getData()) {
                String customerId = customer.getId();

                // Get all charges for the customer, including test charges
                List<Charge> charges = getAllCustomerCharges(customerId);

                // Filter charges to include only approved charges
                List<Charge> approvedCharges = charges.stream()
                        .filter(charge -> "succeeded".equals(charge.getStatus()))
                        .collect(Collectors.toList());

                // Store the transactions in the data structure
                customerTransactions.put(customerId, approvedCharges);

                // Example: Print the customer ID and their transactions

            }

            return customerTransactions;
        } catch (StripeException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private List<Charge> getAllCustomerCharges(String customerId) throws StripeException {
        List<Charge> charges = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("limit", 10); // Set the number of charges to retrieve per page
        params.put("expand", Arrays.asList("data.payment_intent")); // Include payment_intent in the response

        ChargeCollection chargeCollection = Charge.list(params);

        // Iterate over the charges and extract the transaction data as needed
        for (Charge charge : chargeCollection.getData()) {
            // Include only test charges or charges with a payment_intent (live charges)
            if (charge.getPaymentIntent() != null || charge.getLivemode()) {
                charges.add(charge);
            }
        }

        return charges;
    }

}

//package com.example.demo.web.controller;
//
//import com.example.demo.dto.CreatePayment;
//import com.example.demo.dto.CreatePaymentResponse;
//import com.stripe.exception.StripeException;
//import com.stripe.model.PaymentIntent;
//import com.stripe.param.PaymentIntentCreateParams;
//import lombok.Value;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//@RestController
//public class PaymentController {
//
////    @PostMapping("/create-payment-intent")
////    public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePayment createPayment) throws StripeException {
////        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
////                .setCurrency("usd")
////                .setAmount(10L)
////                .build();
////        // Create a PaymentIntent with the order amount and currency
////        PaymentIntent intent = PaymentIntent.create(createParams);
////
////        return new CreatePaymentResponse(intent.getClientSecret());
////    }
//
//
//
//}