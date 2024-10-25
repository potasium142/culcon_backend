package com.culcon.backend.controllers.guest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class GuestController {

    @Operation(tags = "Permission Test", summary = "Test permission for guest")
    @GetMapping("/test_permission")
    public String permissionTest() {
        return "sucess";
    }
}
