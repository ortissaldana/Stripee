package com.example.demo.web.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class StripeController {
    // Define tu clave secreta de API de Stripe
    private static final String STRIPE_SECRET_KEY = "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";

    // Base de datos ficticia para almacenar los ID de los clientes
    private Map<String, String> customerDatabase = new HashMap<>();

    @GetMapping("/create-customer")
    public Map<String, String> createCustomer() {
        Stripe.apiKey = STRIPE_SECRET_KEY;

        try {
            // Crea un nuevo cliente en Stripe
            CustomerCreateParams createParams = CustomerCreateParams.builder()
                    .build();

            Customer customer = Customer.create(createParams);

            // Almacena el ID del cliente en la base de datos
            customerDatabase.put(customer.getId(), customer.getId());

            // Retorna el ID del cliente creado en un objeto JSON
            Map<String, String> response = new HashMap<>();
            response.put("customerId", customer.getId());
            return response;
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/create-setupp-intent")
    public String createSetupIntent() {
        Stripe.apiKey = STRIPE_SECRET_KEY;

        // Obtiene el ID del cliente desde la base de datos o cualquier otro almacenamiento persistente
        String customerId = customerDatabase.get("ID_DEL_CLIENTE"); // Reemplaza con la forma de obtener el ID del cliente

        if (customerId == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        try {
            // Crea un nuevo intento de configuración en Stripe para el cliente
            SetupIntentCreateParams createParams = SetupIntentCreateParams.builder()
                    .setCustomer(customerId)
                    .addPaymentMethodType("bancontact")
                    .addPaymentMethodType("card")
                    .addPaymentMethodType("ideal")
                    .build();

            SetupIntent setupIntent = SetupIntent.create(createParams);

            // Retorna el cliente secreto del intento de configuración
            return setupIntent.getClientSecret();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
