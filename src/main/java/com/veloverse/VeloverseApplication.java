package com.veloverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starting point of the VeloVerse web application.
 *
 * <p>The code is split into layers, and a web request passes through them in this order:
 * controller (handles the URL and picks a Thymeleaf template)
 * -> service (application logic, runs inside a database transaction)
 * -> repository (reads rows using Spring Data JPA)
 * -> PostgreSQL database. The rows come back as model (entity) objects.
 */
// @SpringBootApplication switches on Spring Boot's auto-configuration and scans this package and
// its sub-packages for controllers, services and repositories, so Spring can create and connect them.
@SpringBootApplication
public class VeloverseApplication {

	// main is the method Java runs first: it starts Spring and the built-in web server.
	public static void main(String[] args) {
		SpringApplication.run(VeloverseApplication.class, args);
	}

}
