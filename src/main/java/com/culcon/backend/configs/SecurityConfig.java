package com.culcon.backend.configs;

import com.culcon.backend.exceptions.CustomAccessDeniedHandler;
import com.culcon.backend.services.authenticate.UserAuthService;
import com.culcon.backend.services.helper.UserHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.security.auth.login.AccountNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final UserAuthService userAuthService;
	private final ObjectMapper objectMapper;
	private final LogoutHandler logoutHandler;
	private final UserHelper userHelper;

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http)
		throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.cors(x -> x.configurationSource(corsConfigurationSource()))
			.headers(header -> {
				header.frameOptions(frame -> {
					frame.disable();
					frame.sameOrigin();
				});
			})
			.authorizeHttpRequests(
				request -> request
					.requestMatchers(
						"/debug/**",
						"/api/auth/**",
						"/swagger-ui/**",
						"/api/public/**",
						"/h2-console/**",
						"/v3/api-docs/**",
						"/oauth2/**",
							"/api/payment/vnpay/capture"
					)
					.permitAll()
					.requestMatchers(
						"/api/customer/**",
						"/api/payment/**"
					)
					.hasAnyAuthority("CUSTOMER")
					.anyRequest()
					.authenticated())
			.exceptionHandling(
				e -> e.accessDeniedHandler(customAccessDeniedHandler)
					.authenticationEntryPoint(
						new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
			.sessionManagement(
				manager
					-> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/oauth2/authorization/google")
				.successHandler(authenticationSuccessHandler())
				.failureHandler(authenticationFailureHandler()))
			.logout(
				logout -> logout
					.logoutUrl("/api/v1/auth/logout")
					.addLogoutHandler(logoutHandler)
					.logoutSuccessHandler(
						(request,
						 response,
						 authentication)
							-> SecurityContextHolder.clearContext()));

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userAuthService.userDetailsServices());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
		throws Exception {
		return config.getAuthenticationManager();
	}

	@Override
	public void addCorsMappings(@NonNull
	                            CorsRegistry registry) {
		registry
			.addMapping("/api/**")
			.allowedOrigins("*")
			.allowedOrigins("/**")
			.allowedOrigins("**")
			.allowedOrigins("/oauth2/**")
			.allowedOrigins("http://localhost:8080")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.allowedHeaders("*")
			.allowedHeaders("/oauth2/**")
			.allowedHeaders("/**")
			.allowedHeaders("**")
			.exposedHeaders("X-Get-Header");
	}


	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
		configuration.setExposedHeaders(List.of("x-auth-token"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);
		source.registerCorsConfiguration("*", configuration);
		source.registerCorsConfiguration("/api/**", configuration);
		source.registerCorsConfiguration("/oauth2/**", configuration);
		return source;
	}


	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return (request, response, authentication) -> {
			if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
				String email = oauth2Token.getPrincipal().getAttribute("email");

				try {
					var token = userHelper.loginByEmail(email.trim());

//					response.setCharacterEncoding("UTF-8");
//					response.setContentType("application/json");
//
//					PrintWriter out = response.getWriter();
//					out.print(objectMapper.writeValueAsString(
//						Map.of("token", token)
//					));
//
//					out.flush();

					System.out.println(token);
					String redirectUrl = "http://localhost:3000/token?value=" + token;


					response.sendRedirect(redirectUrl);

				} catch (AccountNotFoundException e) {
//					response.setCharacterEncoding("UTF-8");
//
//					response.setContentType("application/json");
//
//					PrintWriter out = response.getWriter();
//					out.print("{\"message\": \"There's no account linked to the service, please create an account with the email\"," +
//						" \"email\": \"" + email + "\"" + "}");
//					out.flush();

					String redirectUrl = "http://localhost:3000/token";

					response.sendRedirect(redirectUrl);
				}


			}
		};
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return (request, response, exception) -> {
			// Set response content type as JSON
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			// Print the custom error message
			try (PrintWriter out = response.getWriter()) {
				out.print("{\"error\": \"Something went wrong, please log out and try again\"}");
				out.flush();
			}
		};
	}

}
