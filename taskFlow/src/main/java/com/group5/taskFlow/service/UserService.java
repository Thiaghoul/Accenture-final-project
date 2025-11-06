package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.UserRegisterResponse;
import com.group5.taskFlow.dto.UserRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.UserRepository;
import com.group5.taskFlow.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void registerUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        UserModels userModels = new UserModels();
        userModels.setEmail(userRequest.getEmail());
        userModels.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        userModels.setFirstName(userRequest.getFirstName());
        userModels.setLastName(userRequest.getLastName());
        userModels.setRoles(userRequest.getRoles().stream().map(UserRoles::name).collect(Collectors.toSet()));

        userRepository.save(userModels);
    }

    public UserResponse save(UserRequest userRequest) {
        UserModels userModels = new UserModels();
        userModels.setEmail(userRequest.getEmail());
        userModels.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        userModels.setFirstName(userRequest.getFirstName());
        userModels.setLastName(userRequest.getLastName());
        userModels.setRoles(userRequest.getRoles().stream().map(UserRoles::name).collect(Collectors.toSet()));

        UserModels savedUser = userRepository.save(userModels);

        return toUserResponse(savedUser);
    }

    public UserRegisterResponse authenticateUser(String email, String password) {
        UserModels user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new UserRegisterResponse(user.getEmail(), token);
    }

    public java.util.List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
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
