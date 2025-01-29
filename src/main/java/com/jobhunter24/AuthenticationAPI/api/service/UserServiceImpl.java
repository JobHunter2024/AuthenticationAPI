package com.jobhunter24.AuthenticationAPI.api.service;

import com.jobhunter24.AuthenticationAPI.api.dto.LoginDto;
import com.jobhunter24.AuthenticationAPI.api.dto.RegisterDto;
import com.jobhunter24.AuthenticationAPI.api.entity.User;
import com.jobhunter24.AuthenticationAPI.api.exception.DuplicateEmailException;
import com.jobhunter24.AuthenticationAPI.api.exception.DuplicateUsernameException;
import com.jobhunter24.AuthenticationAPI.api.exception.UserNotFoundException;
import com.jobhunter24.AuthenticationAPI.api.repository.UserRepository;
import com.jobhunter24.AuthenticationAPI.api.security.JwtUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtils = jwtUtils;
    }

    @Override
    public User createUser(User user) {

        if (userRepository.existsByUsername(user.getUsername()))
            throw new DuplicateUsernameException("Username already in use");

        if (userRepository.existsByEmail(user.getEmail()))
            throw new DuplicateEmailException("Email already in use");

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id); // Will throw if user doesn't exist

        if (!user.getUsername().equals(existingUser.getUsername())) // if username is updated
            if (userRepository.existsByUsername(user.getUsername())) // verify that the new username doesn't already exist in the DB
                throw new DuplicateUsernameException("Username already in use"); // otherwise throw appropriate exception

        existingUser.setUsername(user.getUsername());

        if (!user.getEmail().equals(existingUser.getEmail())) // if email is updated
            if (userRepository.existsByEmail(user.getEmail())) // verify that the new email doesn't already exist in the DB
                throw new DuplicateEmailException("Email already in use"); // otherwise throw appropriate exception

        existingUser.setEmail(user.getEmail());

        user.setPassword(hashPassword(user.getPassword()));
        existingUser.setRole(user.getRole());

        return userRepository.save(existingUser);
    }

    @Override
    public User patchUser(Long id, User user) {
        User existingUser = getUserById(id); // Will throw if user doesn't exist

        if (user.getUsername() != null) {
            if (userRepository.existsByUsername(user.getUsername()))
                throw new DuplicateUsernameException("Username already in use");

            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            if (userRepository.existsByEmail(user.getEmail()))
                throw new DuplicateEmailException("Email already in use");

            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            user.setPassword(hashPassword(user.getPassword()));
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Override
    public void registerUser(RegisterDto request) {
        User user = User.builder()
                .email(request.getEmail())
                .role(User.Role.USER)
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        user.setPassword(hashPassword(user.getPassword()));

        createUser(user);
    }

    @Override
    public String loginUser(LoginDto loginDto) {
        User existingUser = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));

        // Verify password using BCrypt
        if (!passwordEncoder.matches(loginDto.getPassword(), existingUser.getPassword())) {
            throw new UserNotFoundException("Invalid username or password");
        }

        // Generate a token or session
        String token = jwtUtils.generateToken(existingUser); // Replace with your token generation logic
        return token;
    }

    // Utility method to hash passwords
    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}