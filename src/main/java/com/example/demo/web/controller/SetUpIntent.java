package com.example.demo.web.controller;

import com.example.demo.dto.CreatePayment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.SetupIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
public class SetUpIntent {

    @Value("${stripe.PUBLISHABLE_KEY}")
    private String publishableKey;
    @Value("${stripe.SECRET_KEY}")
    private String SECRET_KEY;

    @PostMapping("/create-setup-intent")
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


            List<String> metodos = new ArrayList<>();
            metodos.add("card");



            RequestOptions requestOptions = RequestOptions.builder().setApiKey(SECRET_KEY).build();

            Customer customer = Customer.create((Map<String, Object>) null, requestOptions);


            SetupIntentCreateParams createParams = new SetupIntentCreateParams.Builder()

                    //.setCurrency("mxn") // Reemplaza con la moneda deseada
                    //.setAmount(1000L) // Reemplaza con el monto deseado
                    //.setDescription("uuwu")
                    .setCustomer("cus_NrqZuXNiuXF4Ph")

                    .addPaymentMethodType("card")

//
//                    .setPaymentMethod("pm_1N6AmPAW1QMD0rARguKU7ehI")
//                    .setReceiptEmail("ortissaldana@icloud.com")
                    //.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                    .build();


//                    .addAllPaymentMethodType(metodos)
//                    .setPaymentMethodData(PaymentIntentCreateParams.PaymentMethodData.builder()
//                            .putExtraParam("type", "customer_balance")
//                            .build())
//                    .setPaymentMethodOptions(pmo)



//
//              .setPaymentMethod("pm_1N6AmPAW1QMD0rARguKU7ehI")
            //     .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
//                    .setAutomaticPaymentMethods(
//                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//                                    .setEnabled(true)
//                                    .build()
//                    )




            SetupIntent setupIntent= SetupIntent.create(createParams);
            //PaymentIntent confirmedIntent = paymentIntent.confirm();


            return new CreatePaymentResponse(setupIntent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }



    @PostMapping("/create-setup-intenttt")
    public CreatePaymentResponse createPaymentIntent4(@RequestParam("paymentMethodId") String paymentMethodId) {
        try {
            Stripe.apiKey = SECRET_KEY; // Reemplaza con tu Stripe Secret Key

            SetupIntentCreateParams createParams = new SetupIntentCreateParams.Builder()

                    .setCustomer("cus_NrqZuXNiuXF4Ph")
                    .setPaymentMethod("pm_1NEC9lAW1QMD0rAREbajGSqe")
                    .build();



            SetupIntent setupIntent = SetupIntent.create(createParams);
            SetupIntent confirmedIntent = setupIntent.confirm();

            return new CreatePaymentResponse(setupIntent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/config2")
    public ConfigResponse getConfig() {
        return new ConfigResponse(publishableKey);
    }



    // Clase de respuesta para /create-payment-intent
    private static class CreatePaymentResponse {
        private String clientSecret;

        public CreatePaymentResponse(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getClientSecret() {
            return clientSecret;
        }
    }

    // Clase de respuesta para /config
    private static class ConfigResponse {
        private String publishableKey;

        public ConfigResponse(String publishableKey) {
            this.publishableKey = publishableKey;
        }

        public String getPublishableKey() {
            return publishableKey;
        }
    }

    static Long calculateOrderAmount(Object[] items) {
        Long totalAmount = (long) 0.0;

        for (Object item : items) {
            if (item instanceof CreatePayment) {
                CreatePayment currentItem = (CreatePayment) item;
                totalAmount += currentItem.getPrice();
            }
        }

        return totalAmount;
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