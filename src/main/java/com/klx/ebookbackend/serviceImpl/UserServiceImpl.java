package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.UserDao;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.service.UserService;
import com.klx.ebookbackend.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public Optional<User> getMe(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public void changePassword(Integer userId, String newPassword) {
        userDao.changePassword(userId, newPassword);
    }

    @Override
    public boolean isPasswordCorrect(String username, String password) {
        return userDao.validateUser(username, password) == 1;
    }

    @Override
    public boolean isUserEnabled(String username) {
        return userDao.validateUserIsEnabled(username) == 1;
    }

    @Override
    public void logout(Integer userId) {
        HttpSession session = SessionUtils.getSession();
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        return userDao.saveUser(user);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userDao.getAllUsers(pageable);
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userDao.searchUsers(keyword, pageable);
    }

    @Override
    public boolean isUserAdmin(Integer userId) {
        Optional<User> userOptional = userDao.getUserById(userId);
        return userOptional.map(user -> user.getIs_admin() == 1).orElse(false);
    }
}
