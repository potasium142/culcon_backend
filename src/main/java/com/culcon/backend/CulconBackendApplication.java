package com.culcon.backend;

import com.culcon.backend.models.Account;
import com.culcon.backend.repositories.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CulconBackendApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(CulconBackendApplication.class, args);
	}

	@Autowired
	private AccountRepo userRepository;

	@Override
	public void run(ApplicationArguments args) {

		var admin = Account.builder()
			.email("example@admin0")
			// ADMIN
			.password("$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq")
			.phone("0123456799")
			.username("admin")
			.build();

//		if (!userRepository.existsByUsername("admin"))
//			userRepository.save(admin);
	}
}
