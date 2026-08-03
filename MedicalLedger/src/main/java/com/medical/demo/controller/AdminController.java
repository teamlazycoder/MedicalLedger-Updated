
package com.medical.demo.controller;

import com.medical.demo.dto.response.ApiResponse;
import com.medical.demo.model.User;
import com.medical.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    @PutMapping("/users/{id}/status")
    @Operation(summary = "Activate/deactivate user")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(active);
        user = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User status updated", user));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get system statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.findByIsActiveTrue().size());
        stats.put("patients", userRepository.findByRole(com.medical.demo.model.enums.Role.PATIENT).size());
        stats.put("doctors", userRepository.findByRole(com.medical.demo.model.enums.Role.DOCTOR).size());

        return ResponseEntity.ok(ApiResponse.success("System stats retrieved", stats));
    }
}