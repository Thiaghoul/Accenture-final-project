package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.UserRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
