package com.culcon.backend.controllers.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Operation(tags = "Permission Test", summary = "Test permission for admin account")
    @GetMapping("/test_permission")
    public String permissionTest() {
        return "sucess";
    }
}
