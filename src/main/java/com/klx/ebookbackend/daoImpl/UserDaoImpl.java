package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.UserDao;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public int validateUser(String username, String password) {
        int result = userRepository.validateUser(username, password);
        System.out.println("username: "  + username + "  password: " + password + "  validateUser result: " + result); // Debug line
        return result;
    }

    @Override
    public int validateUserIsEnabled(String username) {
        return userRepository.validateUserIsEnabled(username);
    }

    @Override
    public void changePassword(int userId, String newPassword) {
        userRepository.changePassword(userId, newPassword);
    }

    @Override
    public User findUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
