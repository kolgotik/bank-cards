package com.example.bankcards.controller;

import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for administrative user management operations.
 * Provides endpoints to create a new user and promote an existing user to admin role.
 * All methods are accessible only by users with the ADMIN role {@link Role}.
 */
@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Promotes a user to the ADMIN role by their ID.
     *
     * @param id The ID of the user to be promoted
     * @return HTTP 200 OK if successful
     */
    @PatchMapping("/make-admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> makeAdmin(@PathVariable Long id) {
        adminService.makeAdmin(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Creates a new user with the provided registration data.
     *
     * @param request The user registration data in the form of {@link RegistrationRequest}
     * @return HTTP 200 OK if the user was created successfully
     */
    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createUser(@RequestBody RegistrationRequest request) {
        adminService.createUser(request);
        return ResponseEntity.ok().build();
    }
}
