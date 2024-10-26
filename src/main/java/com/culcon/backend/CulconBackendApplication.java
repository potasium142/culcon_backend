package com.culcon.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.Role;
import com.culcon.backend.repositories.AccountRepo;

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
                .email("example@admin")
                // ADMIN
                .password("$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq")
                .role(Role.ADMIN)
                .username("admin")
                .build();

        if (!userRepository.existsByUsername("admin"))
            userRepository.save(admin);
    }
}
