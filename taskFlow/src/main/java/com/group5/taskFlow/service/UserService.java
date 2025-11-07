package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.AuthResponse;
import com.group5.taskFlow.dto.UserRegisterRequest;
import com.group5.taskFlow.dto.UserRegisterResponse;
import com.group5.taskFlow.dto.UserRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.model.PasswordResetToken;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.PasswordResetTokenRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import com.group5.taskFlow.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public AuthResponse registerUser(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        UserModels userModels = new UserModels();
        userModels.setEmail(request.getEmail());
        userModels.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userModels.setFirstName(request.getFirstName());
        userModels.setLastName(request.getLastName());
        userModels.setRoles(Collections.singleton(UserRoles.USER.name())); // Default role

        UserModels savedUser = userRepository.save(userModels);
        String token = jwtTokenProvider.generateToken(savedUser.getEmail());
        return new AuthResponse(toUserResponse(savedUser), token);
    }

    public AuthResponse authenticateUser(String email, String password) {
        UserModels user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(toUserResponse(user), token);
    }

    public UserResponse getCurrentUser(String email) {
        UserModels user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return toUserResponse(user);
    }

    public java.util.List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        UserModels user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return toUserResponse(user);
    }

    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        UserModels existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        existingUser.setEmail(userRequest.getEmail());
        existingUser.setFirstName(userRequest.getFirstName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setRoles(userRequest.getRoles().stream().map(UserRoles::name).collect(Collectors.toSet()));

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        }

        UserModels updatedUser = userRepository.save(existingUser);
        return toUserResponse(updatedUser);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public void createPasswordResetTokenForUser(String email) {
        UserModels user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
        emailService.sendSimpleMessage(email, "Password Reset Request", "To reset your password, click the link below:\n" + "http://localhost:8080/reset-password?token=" + token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = validatePasswordResetToken(token);
        UserModels user = passwordResetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    private PasswordResetToken validatePasswordResetToken(String token) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if (passToken == null) {
            throw new IllegalArgumentException("Invalid token.");
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new IllegalArgumentException("Token has expired.");
        }
        return passToken;
    }

    private UserResponse toUserResponse(UserModels userModels) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userModels.getId());
        userResponse.setEmail(userModels.getEmail());
        userResponse.setFirstName(userModels.getFirstName());
        userResponse.setLastName(userModels.getLastName());
        userResponse.setRoles(userModels.getRoles().stream().map(UserRoles::valueOf).collect(Collectors.toSet()));
        return userResponse;
    }
}