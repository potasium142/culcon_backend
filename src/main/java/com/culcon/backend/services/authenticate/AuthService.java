package com.culcon.backend.services.authenticate;

import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.user.Account;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticate);

    Account getUserInformation(HttpServletRequest request);

    AuthenticationResponse registerCustomer(CustomerRegisterRequest request);
}
