package com.example.demo.web.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodListParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/payment-methods2")
public class Cards {
    @Value("${stripe.PUBLISHABLE_KEY}")
    private String publishableKey;
    @Value("${stripe.SECRET_KEY}")
    private String SECRET_KEY;


    @GetMapping
    public List<Map<String, Object>> getCardPaymentMethods() {
        // Configurar la clave secreta de Stripe
        Stripe.apiKey = SECRET_KEY;

        // ID del cliente
        String customerId = "cus_NrqZuXNiuXF4Ph"; // Reemplaza con el ID de tu cliente

        List<Map<String, Object>> cardPaymentMethods = new ArrayList<>();

        try {
            // Obtener los métodos de pago tipo tarjeta asociados al cliente
            List<PaymentMethod> paymentMethods = PaymentMethod.list(
                    PaymentMethodListParams.builder()
                            .setCustomer(customerId)
                            .setType(PaymentMethodListParams.Type.CARD)
                            .build()
            ).getData();

            // Agregar los datos de los métodos de pago tipo tarjeta a la lista
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (paymentMethod.getCard() != null) {
                    Map<String, Object> cardData = new HashMap<>();
                    cardData.put("id", paymentMethod.getId());
                    cardData.put("brand", paymentMethod.getCard().getBrand());
                    cardData.put("last4", paymentMethod.getCard().getLast4());
                    cardData.put("exp_month", paymentMethod.getCard().getExpMonth());
                    cardData.put("exp_year", paymentMethod.getCard().getExpYear());
                    cardData.put("customerName", paymentMethod.getBillingDetails().getName());
                    cardPaymentMethods.add(cardData);
                }
            }

        } catch (StripeException e) {
            // Manejar la excepción de Stripe
            e.printStackTrace();
            // También puedes lanzar una excepción personalizada o manejar el error de otra manera
        }

        return cardPaymentMethods;
    }
}

