package com.culcon.backend.controllers.guest;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class GuestController {

  @Operation(tags = "Permission Test", summary = "Test permission for admin guest")
  @GetMapping("/test_permission")
  public String permissionTest() {
    return "sucess";
  }
}
