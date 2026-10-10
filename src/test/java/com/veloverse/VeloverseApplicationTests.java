package com.veloverse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Startup ("smoke") test: checks that the whole application can start.
 * @SpringBootTest starts the full Spring application, including the connection to the real
 * PostgreSQL test database veloverse_test (see src/test/resources/config/application.properties),
 * so it needs PostgreSQL running and the DB_PASSWORD environment variable set.
 * The class is package-private (no "public"): JUnit 5 does not need test classes to be public.
 */
@SpringBootTest
class VeloverseApplicationTests {

	/**
	 * @Test marks a method that JUnit runs as a test.
	 * The body is empty on purpose: the test passes if Spring could create every bean
	 * (controllers, services, repositories) and connect to the database, and fails if startup throws an error.
	 */
	@Test
	void contextLoads() {
	}

}
