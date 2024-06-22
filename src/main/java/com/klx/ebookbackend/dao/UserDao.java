package com.klx.ebookbackend.dao;

import com.klx.ebookbackend.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> getAllUsers();
    Optional<User> getUserById(int id);
    User saveUser(User user);
    void deleteUser(int id);
    int validateUser(String username, String password);
    void changePassword(int userId, String newPassword);
    User findUserById(int id);
    User findByUsername(String username);
}
