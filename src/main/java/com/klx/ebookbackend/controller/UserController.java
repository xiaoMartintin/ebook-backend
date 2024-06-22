package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.service.UserService;
import com.klx.ebookbackend.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request, HttpSession session) {
        String username = request.get("username");
        String password = request.get("password");
        boolean loginSuccess = userService.login(username, password);
        Map<String, Object> response = new HashMap<>();

        if (loginSuccess) {
            User user = userService.findByUsername(username);
            session.setAttribute("userId", user.getId());

            // Debug: Print session ID and attributes
//            System.out.println("Session ID: " + session.getId());
//            System.out.println("User ID in session: " + session.getAttribute("userId"));

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
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("message", "Logout successful!");
        response.put("data", null);
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


    private Map<String, Object> createResponse(String message, boolean ok, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("ok", ok);
        response.put("data", data);
        return response;
    }

}
