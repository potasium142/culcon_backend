package com.culcon.backend.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  @Operation(tags = "Permission Test", summary = "Test permission for admin account")
  @GetMapping("/test_permission")
  public String permissionTest() {
    return "sucess";
  }
}
