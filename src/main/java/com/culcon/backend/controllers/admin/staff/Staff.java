package com.culcon.backend.controllers.admin.staff;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.culcon.backend.dtos.auth.StaffRegisterRequest;
import com.culcon.backend.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/staff")
public class Staff {

    private final AuthService authService;

    @Operation(tags = "Permission Test", summary = "Test permission for admin account")
    @GetMapping("/test_permission")
    public String permissionTest() {
        return "sucess";
    }

    @Operation(tags = { "Authentication" })
    @PostMapping("/register")
    public ResponseEntity<Object> registerCustomer(
            @Valid @RequestBody StaffRegisterRequest request) {
        var registerLoginToken = authService.registerStaff(request);
        return new ResponseEntity<>(
                registerLoginToken,
                HttpStatus.OK);
    }
}
