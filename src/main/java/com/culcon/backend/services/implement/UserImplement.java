package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.repositories.user.AccountRepo;
import com.culcon.backend.services.UserService;
import com.culcon.backend.services.authenticate.AuthService;
import com.culcon.backend.services.authenticate.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserImplement implements UserService {

    private final AccountRepo userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public Map<String, Object> updateCustomer(
            CustomerInfoUpdateRequest newUserData,
            HttpServletRequest request) {
        var user = authService.getUserInformation(request);

        user.setEmail(newUserData.email());
        user.setUsername(newUserData.username());
        user.setAddress(newUserData.address());
        user.setPhone(newUserData.phone());
        user.setProfileDescription(newUserData.description());

        var jwtToken = jwtService.generateToken(user);

        user.setToken(jwtToken);

        var returnUser = userRepository.save(user);

        var returnData = new HashMap<String, Object>();

        returnData.put("user_data", returnUser);
        returnData.put("access_token", jwtToken);

        return returnData;
    }


    @Override
    public AuthenticationResponse updateCustomerPassword(CustomerPasswordRequest newUserData, HttpServletRequest request) {
        var user = authService.getUserInformation(request);

        if (passwordEncoder.matches(newUserData.oldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newUserData.password()));
            user = userRepository.save(user);
        }

        var reauthenticateRequest = AuthenticationRequest.builder()
                .password(newUserData.password())
                .username(user.getUsername())
                .build();

        return authService.authenticate(reauthenticateRequest);

    }
}
