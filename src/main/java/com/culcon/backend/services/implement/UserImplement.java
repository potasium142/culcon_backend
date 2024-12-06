package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.CartItemDTO;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.exceptions.custom.OTPException;
import com.culcon.backend.models.user.Account;
import com.culcon.backend.repositories.user.AccountOTPRepo;
import com.culcon.backend.repositories.user.AccountRepo;
import com.culcon.backend.repositories.user.ProductRepo;
import com.culcon.backend.services.UserService;
import com.culcon.backend.services.authenticate.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserImplement implements UserService {

	private final AccountRepo userRepository;
	private final AuthService authService;
	private final PasswordEncoder passwordEncoder;
	private final AccountOTPRepo accountOTPRepo;
	private final AccountRepo accountRepo;
	private final ProductRepo productRepo;

	@Override
	public Account getAccountByEmail(String email) throws AccountNotFoundException {
		return userRepository.findAccountByEmail(email.trim())
			.orElseThrow(() -> new AccountNotFoundException("Account not found"));
	}


	@Override
	public Account updateCustomer(
		CustomerInfoUpdateRequest newUserData,
		HttpServletRequest request) {
		var user = authService.getUserInformation(request);

		user.setEmail(newUserData.email());
		user.setUsername(newUserData.username());
		user.setAddress(newUserData.address());
		user.setPhone(newUserData.phone());
		user.setProfileDescription(newUserData.description());

		return userRepository.save(user);
	}


	@Override
	public AuthenticationResponse updateCustomerPassword(CustomerPasswordRequest newUserData, HttpServletRequest request) {
		var user = authService.getUserInformation(request);

		if (!passwordEncoder.matches(newUserData.oldPassword(), user.getPassword())) {
			throw new NoSuchElementException("Old password does not match");
		}

		user.setPassword(passwordEncoder.encode(newUserData.password()));
		user = userRepository.save(user);

		var reauthenticateRequest = AuthenticationRequest.builder()
			.password(newUserData.password())
			.username(user.getUsername())
			.build();

		return authService.authenticate(reauthenticateRequest);

	}

	@Override
	public void updateCustomerPasswordOTP(String otp, String id, String newPassword) {
		var accountOTP = accountOTPRepo.findByOtpAndAccountId(otp, id)
			.orElseThrow(() -> new OTPException("OTP not found"));

		var sqlTimestamp = Timestamp.valueOf(LocalDateTime.now());
		var isTokenExpire = accountOTP.getOtpExpiration().before(sqlTimestamp);

		if (isTokenExpire) {
			throw new OTPException("OTP expired");
		}

		var account = accountOTP.getAccount();

		account.setPassword(passwordEncoder.encode(newPassword));

		accountRepo.save(account);

		accountOTPRepo.delete(accountOTP);
	}

	@Override
	public List<CartItemDTO> fetchCustomerCart(HttpServletRequest request) {
		// stinky ass function
		return authService.getUserInformation(request)
			.getCart()
			.entrySet().stream()
			.map(CartItemDTO::of)
			.toList();
	}

	@Override
	public CartItemDTO addProductToCart(String productId, Integer amount, HttpServletRequest request) {
		var product = productRepo.findById(productId)
			.orElseThrow(() -> new NoSuchElementException("Product not found incart"));

		var account = authService.getUserInformation(request);

		var itemAmount = account.getCart().getOrDefault(product, 0) + amount;

		if (itemAmount <= 0) {
			account.getCart().remove(product);
		} else {
			account.getCart().put(product, itemAmount);
		}

		accountRepo.save(account);

		return CartItemDTO.builder()
			.product(product)
			.amount(itemAmount).build();
	}

	@Override
	public Map<String, Object> setProductAmountInCart(String productId, Integer amount, HttpServletRequest request) {
		var account = authService.getUserInformation(request);

		var product = account.getCart()
			.keySet()
			.stream()
			.filter(prod -> prod.getId().equals(productId))
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("Product not found in cart"));

		if (amount <= 0) {
			account.getCart().remove(product);
		} else {
			account.getCart().put(product, amount);
		}

		accountRepo.save(account);

		var map = new HashMap<String, Object>();

		map.put("productId", productId);
		map.put("amount", amount);

		return map;
	}

	@Override
	public Boolean removeProductFromCart(String productId, HttpServletRequest request) {
		var account = authService.getUserInformation(request);

		var product = account.getCart()
			.keySet()
			.stream()
			.filter(prod -> prod.getId().equals(productId))
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("Product not found in cart"));

		account.getCart().remove(product);

		account = accountRepo.save(account);

		return !account.getCart().containsKey(product);
	}
}
