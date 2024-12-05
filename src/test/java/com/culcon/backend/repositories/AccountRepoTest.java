package com.culcon.backend.repositories;

import com.culcon.backend.models.user.Account;
import com.culcon.backend.repositories.user.AccountRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
public class AccountRepoTest {
	@Autowired
	private AccountRepo accountRepo;

	@Test
	@DisplayName("Insert account to repository")
	void accountRepo_SaveAccount() {
		var defaultAccount = Account.builder()
			.email("example@admin")
			.password("$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq")
			.phone("0123456789")
			.username("admin")
			.profileDescription("when the imposter is sus")
			.build();

		var saveAccount = accountRepo.save(defaultAccount);
		saveAccount.setId("");

		assertEquals(defaultAccount, saveAccount);
	}
}
