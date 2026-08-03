package com.medical.demo.service.auth;

import com.medical.demo.dto.request.LoginRequest;
import com.medical.demo.dto.request.RegisterRequest;
import com.medical.demo.dto.response.AuthResponse;
import com.medical.demo.dto.response.UserResponse;
import com.medical.demo.exception.BusinessException;
import com.medical.demo.exception.ResourceNotFoundException;
import com.medical.demo.model.Doctor;
import com.medical.demo.model.Patient;
import com.medical.demo.model.RefreshToken;
import com.medical.demo.model.User;
import com.medical.demo.model.enums.Role;
import com.medical.demo.repository.DoctorRepository;
import com.medical.demo.repository.PatientRepository;
import com.medical.demo.repository.RefreshTokenRepository;
import com.medical.demo.repository.UserRepository;
import com.medical.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);
        user = userRepository.save(user);

        if (request.getRole() == Role.PATIENT) {
            createPatientProfile(user, request);
        } else if (request.getRole() == Role.DOCTOR) {
            createDoctorProfile(user, request);
        }

        log.info("User registered successfully: {}", user.getEmail());
        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getEmail());
        return generateAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (storedToken.isExpired() || storedToken.getRevoked()) {
            refreshTokenRepository.delete(storedToken);
            throw new BusinessException("Refresh token expired or revoked");
        }

        User user = storedToken.getUser();
        refreshTokenRepository.delete(storedToken);
        return generateAuthResponse(user);
    }

    @Override
    @Transactional
    public void logout(String token) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        refreshTokenRepository.revokeAllUserTokens(user);
        SecurityContextHolder.clearContext();
        log.info("User logged out: {}", email);
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    private void createPatientProfile(User user, RegisterRequest request) {
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setBloodType(request.getBloodType());
        patient.setContactNumber(request.getContactNumber());
        patient.setAddress(request.getAddress());
        patientRepository.save(patient);
    }

    private void createDoctorProfile(User user, RegisterRequest request) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setHospitalAffiliation(request.getHospitalAffiliation());
        doctor.setYearsOfExperience(request.getYearsOfExperience());
        doctor.setQualifications(request.getQualifications());
        doctorRepository.save(doctor);
    }

    private AuthResponse generateAuthResponse(User user) {
        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId());
        String refreshToken = generateRefreshToken(user);

        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userResponse)
                .build();
    }

    private String generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setIsActive(user.getIsActive());
        response.setLastLogin(user.getLastLogin());
        response.setCreatedAt(user.getCreatedAt());

        if (user.getRole() == Role.PATIENT) {
            response.setProfileCompleted(user.getPatient() != null);
        } else if (user.getRole() == Role.DOCTOR) {
            response.setProfileCompleted(user.getDoctor() != null);
        }

        return response;
    }
}