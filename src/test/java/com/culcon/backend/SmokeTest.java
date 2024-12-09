package com.culcon.backend;

import com.culcon.backend.controllers.auth.AuthController;
import com.culcon.backend.repositories.AccountRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmokeTest {

	@Autowired
	private AuthController authController;

	@Autowired
	private AccountRepo userRepository;

//	@TestConfiguration
//	class TestConfig {
//		@EventListener(ApplicationStartedEvent.class)
//		public void setupAccount() {
//
//			var admin = Account.builder()
//				.email("example@test")
//				// ADMIN
//				.password("$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq")
//				.phone("0969996669")
//				.username("test_account")
//				.build();
//
//			userRepository.save(admin);
//		}
//	}

	@Test
	@DisplayName("Smoke test")
	void contextLoads() {
		Assertions.assertNotNull(authController);
	}
}
