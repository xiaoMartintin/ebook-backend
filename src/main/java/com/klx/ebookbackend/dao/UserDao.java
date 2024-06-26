package com.klx.ebookbackend.dao;

import com.klx.ebookbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserDao {
    Page<User> getAllUsers(Pageable pageable);
    Optional<User> getUserById(int id);
    User saveUser(User user);
    void deleteUser(int id);
    int validateUser(String username, String password);
    int validateUserIsEnabled(String username);
    void changePassword(int userId, String newPassword);
    User findUserById(int id);
    User findByUsername(String username);
    Page<User> searchUsers(String keyword, Pageable pageable);
}
