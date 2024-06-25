package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.User;

import java.util.*;

public interface UserService {
    Optional<User> getMe(Integer userId);
    void changePassword(Integer userId, String newPassword);
    boolean isPasswordCorrect(String username, String password);
    boolean isUserEnabled(String username);
    void logout(Integer userId);
    Optional<User> getUserById(Integer userId);
    User findByUsername(String username);
    User saveUser(User user);
    List<User> getAllUsers();
    List<User> searchUsers(String search);
    boolean isUserAdmin(Integer userId);
}
