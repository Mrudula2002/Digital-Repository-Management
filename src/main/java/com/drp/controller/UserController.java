package com.drp.controller;

import com.drp.dto.request.RegisterUserRequest;
import com.drp.dto.request.UpdateUserRequest;
import com.drp.dto.response.ApiResponse;
import com.drp.dto.response.UserResponse;
import com.drp.entity.User;
import com.drp.security.SecurityUtils;
import com.drp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody RegisterUserRequest request) {
        User actor = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        return ApiResponse.success("User created", userService.createUser(request, actor));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        User actor = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        return ApiResponse.success("User updated", userService.updateUser(id, request, actor));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivateUser(@PathVariable Long id) {
        User actor = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        userService.deactivateUser(id, actor);
        return ApiResponse.success("User deactivated", null);
    }
}
