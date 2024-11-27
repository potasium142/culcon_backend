package com.culcon.backend;

import com.culcon.backend.models.user.Account;
import com.culcon.backend.models.user.Role;
import com.culcon.backend.repositories.user.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CulconBackendApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(CulconBackendApplication.class, args);
    }

    @Autowired
    private AccountRepo userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var admin = Account.builder()
                .id("53695356-91be-4772-a364-a6b32a51f8b5")
                .email("example@admin0")
                // ADMIN
                .password("$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq")
                .role(Role.CUSTOMER)
                .phone("0123456799")
                .username("admin")
                .build();

//		if (!userRepository.existsByUsername("admin"))
//			userRepository.save(admin);
    }
}
