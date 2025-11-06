package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.UserRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
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
