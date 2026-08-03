package com.medical.demo.service.auth;

import com.medical.demo.dto.request.LoginRequest;
import com.medical.demo.dto.request.RegisterRequest;
import com.medical.demo.dto.response.AuthResponse;
import com.medical.demo.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String token);
    UserResponse getCurrentUser(String username);
}
