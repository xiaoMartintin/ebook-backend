package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.UserDao;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.service.UserService;
import com.klx.ebookbackend.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
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


    public List<User> getAllUsers(){
        return userDao.getAllUsers();
    }

    @Override
    public List<User> searchUsers(String search) {
        return userDao.searchUsers(search);
    }

}
