package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.klx.ebookbackend.service.SessionTimerService;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
// 不需要添加 @Scope 注解，保持默认的 @Scope("singleton") 即可
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    private SessionTimerService sessionTimerService;

    @PostMapping("/user/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        System.out.println(request);
        String username = request.get("username");
        String nickname = request.getOrDefault("nickname", username);//昵称默认为用户名
        String password = request.get("password");
        String email = request.get("email");
        System.out.println(username);
        System.out.println(password);
        System.out.println(email);
        Map<String, Object> response = new HashMap<>();

        //检查有没有重名
        if (userService.findByUsername(username) != null) {
            response.put("ok", false);
            response.put("message", "Username already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setNickname(nickname); // 设置昵称
        newUser.setBalance(10000.0); // Assuming default balance
        newUser.setIs_admin(0); // Assuming default is non-admin
        newUser.setIs_enabled(1); // Assuming default is enabled
        userService.saveUser(newUser);

        // 调用存储过程来设置密码
        userService.changePassword(newUser.getId(), password);

        response.put("ok", true);
        response.put("message", "Registration successful!");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request, HttpSession session) {
        String username = request.get("username");
        String password = request.get("password");
        boolean isPasswordCorrect = userService.isPasswordCorrect(username, password);
        boolean isUserEnabled = userService.isUserEnabled(username);
        Map<String, Object> response = new HashMap<>();

        if (!isUserEnabled) {
            response.put("ok", false);
            response.put("message", "您的账号已经被禁用");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (isPasswordCorrect) {
            User user = userService.findByUsername(username);
            session.setAttribute("userId", user.getId());

            // 登录成功后，启动计时器
            sessionTimerService.startTimer();

            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("nickname", user.getNickname());

            response.put("ok", true);
            response.put("message", "Login successful!");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } else {
            response.put("ok", false);
            response.put("message", "Invalid username or password.");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @PutMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        // 停止计时器，并获取会话持续时间
        long sessionDuration = sessionTimerService.stopTimer();

        // 使当前会话失效
        session.invalidate();

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("message", "Logout successful!");
        response.put("sessionDuration", sessionDuration + " milliseconds"); // 返回会话持续时间

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getMe(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Optional<User> userOptional = userService.getMe(userId);
        if (userOptional.isPresent()) {
            System.out.println("user: "+userOptional.get().getUsername());
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/user/me/password")
    public ResponseEntity<?> changeMyPassword(@RequestBody Map<String, String> request, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> unauthorizedResponse = new HashMap<>();
            unauthorizedResponse.put("ok", false);
            unauthorizedResponse.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedResponse);
        }
        String newPassword = request.get("password");
        userService.changePassword(userId, newPassword);

        return ResponseEntity.ok(createResponse("Password changed successfully", true, null));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null || !userService.isUserAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<User> users;

        if (search != null && !search.isEmpty()) {
            users = userService.searchUsers(search, pageable);
        } else {
            users = userService.getAllUsers(pageable);
        }

        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer userId, @RequestBody Map<String, Boolean> request, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("userId");
        if (adminId == null || !userService.isUserAdmin(adminId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createResponse("Access denied", false, null));
        }

        Boolean isEnabled = request.get("is_enabled");
        Optional<User> userOptional = userService.getUserById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getIs_enabled() != (isEnabled ? 1 : 0)) {
                user.setIs_enabled(isEnabled ? 1 : 0);
                userService.saveUser(user);
                return ResponseEntity.ok().body(createResponse("User status updated successfully", true, null));
            } else {
                return ResponseEntity.ok().body(createResponse("User status is the same, no update needed", true, null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponse("User not found", false, null));
        }
    }

    private Map<String, Object> createResponse(String message, boolean ok, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("ok", ok);
        response.put("data", data);
        return response;
    }
}
