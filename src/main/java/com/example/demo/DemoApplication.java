package com.example.demo;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.Proxy;

@SpringBootApplication
public class DemoApplication {




	public static void main(String[] args) {



		SpringApplication.run(DemoApplication.class, args);
	}

}
