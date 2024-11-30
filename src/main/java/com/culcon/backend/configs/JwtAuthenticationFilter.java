package com.culcon.backend.configs;

import com.culcon.backend.repositories.user.AccountRepo;
import com.culcon.backend.services.authenticate.JwtService;
import com.culcon.backend.services.authenticate.UserAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserAuthService userService;
	private final AccountRepo accountRepo;

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain)
		throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		final String jwtToken;
		final String username;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		jwtToken = authHeader.substring(7);

		username = accountRepo.findByToken(jwtToken).orElseThrow(
			() -> new UsernameNotFoundException("User not found")
		).getUsername();


		if (SecurityContextHolder
			.getContext()
			.getAuthentication() == null) {

			UserDetails userDetails = this.userService.userDetailsServices().loadUserByUsername(username);

			if (jwtService.isTokenValid(jwtToken, userDetails)) {
				var authToken = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities());
				authToken.setDetails(
					new WebAuthenticationDetailsSource()
						.buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}
