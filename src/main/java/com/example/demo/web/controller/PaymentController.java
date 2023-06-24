package com.example.demo.web.controller;
import com.example.demo.dto.CreatePayment;
import com.example.demo.dto.CreatePaymentResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


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
                    .setAmount(100L) // Reemplaza con el monto deseado
                    .setDescription("Pago por transferencia a DriveAI")
                    .setCustomer("cus_NrqZuXNiuXF4Ph")
                    .setReceiptEmail("ortissaldana@icloud.com")
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
    public CreatePaymentResponse createPaymentIntent2(@RequestParam("paymentMethodId") String paymentMethodId, @RequestParam("price") Long price) {
        try {
            Stripe.apiKey = SECRET_KEY; // Reemplaza con tu Stripe Secret Key

            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setCurrency("MXN")
                    .setAmount(price) // Utiliza el precio recibido como parámetro
                    .setCustomer("cus_NrqZuXNiuXF4Ph") // Reemplaza con el ID del cliente
                    .setPaymentMethod(paymentMethodId) // Reemplaza con el ID del método de pago
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(createParams);
            paymentIntent.confirm();
            return new CreatePaymentResponse(paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }





    @GetMapping("/transactions")
    public ResponseEntity<String> getAllTransactions() throws StripeException {
        Stripe.apiKey = SECRET_KEY;

        List<Map<String, Object>> allTransactions = new ArrayList<>();

        CustomerListParams.Builder customerParamsBuilder = CustomerListParams.builder()
                .setLimit(100L); // Establece el número de clientes que se recuperarán por página

        while (true) {
            CustomerCollection customers = Customer.list(customerParamsBuilder.build());

            for (Customer customer : customers.getData()) {
                // Obtén todas las transacciones para el cliente, incluidas las transacciones de prueba
                List<Map<String, Object>> transactions = getAllCustomerTransactions(customer.getId());

                // Agrega las transacciones a la lista general
                allTransactions.addAll(transactions);
            }

            if (!customers.getHasMore()) {
                break;
            }

            customerParamsBuilder.setStartingAfter(customers.getData().get(customers.getData().size() - 1).getId());
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(json.getAsJsonPrimitive().getAsLong()), ZoneOffset.UTC))
                .create();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(allTransactions));
    }

    private List<Map<String, Object>> getAllCustomerTransactions(String customerId) throws StripeException {
        List<Map<String, Object>> transactions = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("limit", 10); // Establece el número de transacciones que se recuperarán por página

        ChargeCollection chargeCollection = Charge.list(params);

        for (Charge charge : chargeCollection.getData()) {
            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("fecha", formatTimestamp(charge.getCreated()));
            transactionData.put("tipo_movimiento", charge.getObject());
            transactionData.put("monto", charge.getAmount());
            transactionData.put("customer_id", customerId);
            transactionData.put("transaccion_id", charge.getId());
            transactionData.put("referencia_pago", charge.getPaymentMethod());

            transactions.add(transactionData);
        }

        return transactions;
    }

    private String formatTimestamp(Long timestamp) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    @GetMapping("/bank-accounts")
    public ResponseEntity<List<Map<String, String>>> getAllCustomerBankAccounts() {
        Stripe.apiKey = SECRET_KEY;

        try {
            CustomerCollection customers = Customer.list((Map<String, Object>) null);

            List<Map<String, String>> bankAccountsList = new ArrayList<>();

            for (Customer customer : customers.getData()) {
                List<PaymentSource> paymentSources = customer.getSources().getData();

                for (PaymentSource paymentSource : paymentSources) {
                    if (paymentSource instanceof BankAccount) {
                        BankAccount bankAccount = (BankAccount) paymentSource;

                        Map<String, String> bankAccountData = new HashMap<>();
                        bankAccountData.put("customer_id", customer.getId());
                        bankAccountData.put("account_number", bankAccount.getLast4());
                        bankAccountData.put("routing_number", bankAccount.getRoutingNumber());
                        bankAccountData.put("bank_name", bankAccount.getBankName());

                        bankAccountsList.add(bankAccountData);
                    }
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bankAccountsList);
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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