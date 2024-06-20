package com.klx.ebookbackend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private Map<String, Map<String, String>> users = new HashMap<>();

    public UserController() {
        // 初始化一些用户数据
        addUser("1", "ebookadmin", "zhangziqian@sjtu.edu.cn", "123456", "ebookadmin");
        addUser("2", "ebookuser1", "zhangziqian@sjtu.edu.cn", "12345678", "ebookuser1");
        addUser("3", "musicminion", "zhang20021014@126.com", "123456", "musicminion");
    }

    private void addUser(String id, String username, String email, String password, String nickname) {
        Map<String, String> user = new HashMap<>();
        user.put("id", id);
        user.put("username", username);
        user.put("email", email);
        user.put("password", password);
        user.put("nickname", nickname);
        users.put(username, user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        Map<String, String> user = users.get(username);
        if (user != null && user.get("password").equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", Integer.parseInt(user.get("id")));
            response.put("username", user.get("username"));
            response.put("email", user.get("email"));
            response.put("nickname", user.get("nickname"));
            response.put("status", "success");
            return response;
        } else {
            return Collections.singletonMap("status", "failure");
        }
    }

    @PutMapping("/logout")
    public Map<String, Object> logout() {
        return Collections.singletonMap("status", "success");
    }

    @GetMapping("/user/me")
    public Map<String, Object> getMe() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("username", "sampleuser");
        user.put("email", "user@example.com");
        return user;
    }

    @PutMapping("/user/me/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> request) {
        return Collections.singletonMap("status", "success");
    }
}
