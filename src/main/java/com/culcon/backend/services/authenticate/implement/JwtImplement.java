package com.culcon.backend.services.authenticate.implement;

import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.services.authenticate.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtImplement implements JwtService {
	private final AccountRepo accountRepo;

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.expiration.access-token}")
	private long jwtExpiration;

	@Override
	public String extractId(String jwtToken) {
		return extractClaim(jwtToken, Claims::getSubject);
	}

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(extraClaims, userDetails, jwtExpiration);
	}

	private String buildToken(
		Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
		var userId = accountRepo.findByUsername(userDetails.getUsername()).orElseThrow(
			() -> new UsernameNotFoundException("Username not found")
		).getId();
		return Jwts.builder()
			.claims(extraClaims)
			.subject(userId)
			.claim("role", populateAuthorities(userDetails.getAuthorities()))
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
			.signWith(getSigningKey())
			.compact();
	}

	private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Set<String> authoritiesSet = new HashSet<>();
		for (GrantedAuthority authority : authorities) {
			authoritiesSet.add(authority.getAuthority());
		}
		return String.join(",", authoritiesSet);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		var isIdExist = accountRepo.existsById(extractId(token));
		return isIdExist && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
			.verifyWith((SecretKey) getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
