package com.payflow.user.controller;

import com.payflow.user.dto.RegisterRequest;
import com.payflow.user.dto.UserResponse;
import com.payflow.user.entity.User;
import com.payflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = userService.createUser(
                request.name(),
                request.email(),
                request.password()
        );
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus()
        );
    }
}