package com.culcon.backend;

import com.culcon.backend.controllers.auth.AuthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmokeTest {

	@Autowired
	private AuthController authController;

	@Test
	@DisplayName("Smoke test")
	void contextLoads() {
		Assertions.assertNotNull(authController);
	}
}
