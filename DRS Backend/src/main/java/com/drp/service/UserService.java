package com.drp.service;

import com.drp.dto.request.RegisterUserRequest;
import com.drp.dto.request.UpdateUserRequest;
import com.drp.dto.response.UserResponse;
import com.drp.entity.ActivityAction;
import com.drp.entity.Role;
import com.drp.entity.User;
import com.drp.exception.BadRequestException;
import com.drp.exception.ResourceNotFoundException;
import com.drp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return UserResponse.from(findUserById(id));
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional
    public UserResponse createUser(RegisterUserRequest request, User actor) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);        user.setActive(true);

        User saved = userRepository.save(user);
        activityLogService.log(actor, ActivityAction.CREATE_USER, "User", saved.getId(),
                "Created user: " + saved.getUsername());

        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, User actor) {
        User user = findUserById(id);

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BadRequestException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User saved = userRepository.save(user);
        activityLogService.log(actor, ActivityAction.UPDATE_USER, "User", saved.getId(),
                "Updated user: " + saved.getUsername());

        return UserResponse.from(saved);
    }

    @Transactional
    public void deactivateUser(Long id, User actor) {
        User user = findUserById(id);
        user.setActive(false);
        userRepository.save(user);
        activityLogService.log(actor, ActivityAction.UPDATE_USER, "User", user.getId(),
                "Deactivated user: " + user.getUsername());
    }
}
