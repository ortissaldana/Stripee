package com.example.demo.web.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;

@RestController
@CrossOrigin
public class AcdcController {

    String client_id = "AWMq4PV_1Y0FjpeopEuZ9EbHImVqPAvNVCeRm35j-6zG2SRO7zQop8o7ozfBVKJABCFOkQ4MY-CWYWsx";
    String App_secret = "EIv2FzqGXlUYqPA34qUz3chp_PKAxzhxnbvCs0kCVV4bCd8hg5bC0G-d6U_b_ULDLYki_x-DD0Z2GFpi";

    @RequestMapping(value = "/")
    public String index() {
        return "/advanced-integration/public/index";
    }

    @RequestMapping("/api/token")
    public String generateClientToken() throws JSONException {
        String accessToken = "Bearer " + generateAccessToken();
        String url = "https://api-m.sandbox.paypal.com/v1/identity/generate-token";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        headers.set("Accept-Language", "en_US");
        String req = "{}";
        HttpEntity<String> requestHeader = new HttpEntity<>(req, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestHeader, String.class);
        JSONObject jsonResponse = new JSONObject(response.getBody());
        return jsonResponse.toString();
    }

    @RequestMapping("/api/orders")
    public String createOrder() throws JSONException {
        String accessToken = "Bearer " + generateAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        JSONObject order = new JSONObject();
        JSONObject purchaseUnits = new JSONObject();
        JSONObject amount = new JSONObject();
        amount.put("value", "100.00");
        amount.put("currency_code", "USD");
        purchaseUnits.put("amount", amount);
        order.put("purchase_units", Arrays.asList(purchaseUnits));
        order.put("intent", "capture");

        String req = "{\"intent\": \"CAPTURE\"," +
                "\"purchase_units\": [" +
                "  {" +
                "    \"amount\": {" +
                "      \"currency_code\": \"USD\"," +
                "      \"value\": \"83.04\"" +
                "    }" +
                "  }" +
                "]}";

        HttpEntity requestHeader = new HttpEntity(req, headers);
        String url = "https://api-m.sandbox.paypal.com/v2/checkout/orders";
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestHeader, String.class);
        String responseString = response.toString();
        JSONObject jsonResponse = new JSONObject(response.getBody());
        String orderId = jsonResponse.getString("id");
        return jsonResponse.toString();
    }

    public String capturePayment(String accessToken, String orderId) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        String req = "{}";
        HttpEntity<String> requestHeader = new HttpEntity<>(req, headers);
        String url = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture";
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestHeader, String.class);
        JSONObject jsonResponse = new JSONObject(response.getBody());
        return jsonResponse.toString();
    }

    public String generateAccessToken() throws JSONException {
        String clientIdSecret = client_id + ":" + App_secret;
        String encodedClientId = Base64.getEncoder().encodeToString(clientIdSecret.getBytes());
        String template = "Basic " + encodedClientId;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", template);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("response_type", "token");
        map.add("ignoreCache", "true");
        HttpEntity<MultiValueMap<String, String>> requestHeader = new HttpEntity<>(map, headers);
        String url = "https://api-m.sandbox.paypal.com/v1/oauth2/token";
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestHeader, String.class);
        JSONObject jsonResponse = new JSONObject(response.getBody());
        String accessToken = jsonResponse.getString("access_token");
        return accessToken;
    }
}
