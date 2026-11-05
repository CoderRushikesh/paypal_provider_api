package com.payment;

import org.springframework.boot.SpringApplication;
<<<<<<< HEAD
=======

>>>>>>> master
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.payment", "com.controller"})
public class PaypalProviderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaypalProviderServiceApplication.class, args);
	}

}
