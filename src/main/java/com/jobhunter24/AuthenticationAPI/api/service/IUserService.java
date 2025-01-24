package com.jobhunter24.AuthenticationAPI.api.service;

import com.jobhunter24.AuthenticationAPI.api.dto.LoginDto;
import com.jobhunter24.AuthenticationAPI.api.entity.User;

import java.util.List;

public interface IUserService {
    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(Long id, User user);

    User patchUser(Long id, User user);

    void deleteUser(Long id);

    void registerUser(User user);

    String loginUser(LoginDto loginDto);
}
