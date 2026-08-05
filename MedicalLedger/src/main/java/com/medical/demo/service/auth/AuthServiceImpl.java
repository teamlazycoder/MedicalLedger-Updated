package com.medical.demo.service.auth;
import com.medical.demo.dto.request.*; import com.medical.demo.dto.response.*;
import com.medical.demo.exception.*; import com.medical.demo.model.*; import com.medical.demo.model.enums.Role;
import com.medical.demo.repository.*; import com.medical.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication; import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime; import java.util.UUID;

@Slf4j @Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository; private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository; private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder; private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override @Transactional
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) throw new BusinessException("Email already registered");
        if(userRepository.existsByUsername(request.getUsername())) throw new BusinessException("Username already taken");
        User user = new User(); user.setUsername(request.getUsername()); user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); user.setRole(request.getRole()); user.setIsActive(true);
        user = userRepository.save(user);
        if(request.getRole()==Role.PATIENT) { Patient p=new Patient(); p.setUser(user); p.setFirstName(request.getFirstName()); p.setLastName(request.getLastName()); p.setDateOfBirth(request.getDateOfBirth()); p.setGender(request.getGender()); p.setBloodType(request.getBloodType()); p.setContactNumber(request.getContactNumber()); p.setAddress(request.getAddress()); patientRepository.save(p); }
        else if(request.getRole()==Role.DOCTOR) { Doctor d=new Doctor(); d.setUser(user); d.setFirstName(request.getFirstName()); d.setLastName(request.getLastName()); d.setSpecialty(request.getSpecialty()); d.setLicenseNumber(request.getLicenseNumber()); d.setHospitalAffiliation(request.getHospitalAffiliation()); d.setYearsOfExperience(request.getYearsOfExperience()); d.setQualifications(request.getQualifications()); doctorRepository.save(d); }
        return generateAuthResponse(user);
    }
    @Override public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new ResourceNotFoundException("User not found"));
        user.setLastLogin(LocalDateTime.now()); userRepository.save(user);
        return generateAuthResponse(user);
    }
    @Override @Transactional public AuthResponse refreshToken(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new BusinessException("Invalid refresh token"));
        if(stored.isExpired()||stored.getRevoked()) { refreshTokenRepository.delete(stored); throw new BusinessException("Refresh token expired"); }
        User user = stored.getUser(); refreshTokenRepository.delete(stored); return generateAuthResponse(user);
    }
    @Override @Transactional public void logout(String token) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
        refreshTokenRepository.revokeAllUserTokens(user); SecurityContextHolder.clearContext();
    }
    @Override public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found with email: "+email));
        return mapToUserResponse(user);
    }
    private AuthResponse generateAuthResponse(User user) {
        String token = jwtTokenProvider.generateToken(user.getEmail(),user.getRole().name(),user.getId());
        String refreshToken = generateRefreshToken(user);
        return AuthResponse.builder().token(token).refreshToken(refreshToken).tokenType("Bearer").expiresIn(86400000L).user(mapToUserResponse(user)).build();
    }
    private String generateRefreshToken(User user) {
        RefreshToken rt = new RefreshToken(); rt.setUser(user); rt.setToken(UUID.randomUUID().toString());
        rt.setExpiresAt(LocalDateTime.now().plusDays(7)); rt.setRevoked(false); refreshTokenRepository.save(rt); return rt.getToken();
    }
    private UserResponse mapToUserResponse(User user) {
        UserResponse r = new UserResponse(); r.setId(user.getId()); r.setUsername(user.getUsername()); r.setEmail(user.getEmail());
        r.setRole(user.getRole()); r.setIsActive(user.getIsActive()); r.setLastLogin(user.getLastLogin()); r.setCreatedAt(user.getCreatedAt());
        if(user.getRole()==Role.PATIENT) r.setProfileCompleted(user.getPatient()!=null);
        else if(user.getRole()==Role.DOCTOR) r.setProfileCompleted(user.getDoctor()!=null);
        return r;
    }
}