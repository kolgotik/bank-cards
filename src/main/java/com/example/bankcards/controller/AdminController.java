package com.example.bankcards.controller;

import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/make-admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> makeAdmin(@PathVariable Long id) {
        adminService.makeAdmin(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createUser(@RequestBody RegistrationRequest request) {
        adminService.createUser(request);
        return ResponseEntity.ok().build();
    }
}
