package com.jobhunter24.AuthenticationAPI.api.controller;

import com.jobhunter24.AuthenticationAPI.api.dto.LoginDto;
import com.jobhunter24.AuthenticationAPI.api.dto.RegisterDto;
import com.jobhunter24.AuthenticationAPI.api.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final IUserService userService;

    public AuthenticationController(IUserService userService) {
        this.userService = userService;
    }

    // Register User - POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody RegisterDto request) {
        userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // HTTP 201
    }

    // Login User - POST /api/users/login
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto) {
        String token = userService.loginUser(loginDto);

        return ResponseEntity.ok(token); // HTTP 200
    }
}
