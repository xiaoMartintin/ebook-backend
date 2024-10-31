package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Optional<User> getMe(Integer userId);
    void changePassword(Integer userId, String newPassword);
    boolean isPasswordCorrect(String username, String password);
    boolean isUserEnabled(String username);
    void logout(Integer userId);
    Optional<User> getUserById(Integer userId);
    User findByUsername(String username);
    User saveUser(User user);
    boolean isUserAdmin(Integer userId);
    Page<User> searchUsers(String keyword, Pageable pageable);
    Page<User> getAllUsers(Pageable pageable);
}
