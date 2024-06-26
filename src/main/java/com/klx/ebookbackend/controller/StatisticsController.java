package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.service.StatisticsService;
import com.klx.ebookbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<?> getPurchaseStatistics(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        Map<String, Object> statistics = statisticsService.getPurchaseStatistics(userId, startDate, endDate, pageIndex, pageSize);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/sales")
    public ResponseEntity<?> getSalesStatistics(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpSession session) {

        Integer adminId = (Integer) session.getAttribute("userId");
        if (adminId == null || !userService.isUserAdmin(adminId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Map<String, Object> salesStats = statisticsService.getSalesStatistics(startDate, endDate, pageIndex, pageSize);
        System.out.println("salesStats: "+salesStats);
        return ResponseEntity.ok(salesStats);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserPurchaseStatistics(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpSession session) {

        Integer adminId = (Integer) session.getAttribute("userId");
        if (adminId == null || !userService.isUserAdmin(adminId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Map<String, Object> userStats = statisticsService.getUserPurchaseStatistics(startDate, endDate, pageIndex, pageSize);
        System.out.println("userStats: "+userStats);
        return ResponseEntity.ok(userStats);
    }
}
