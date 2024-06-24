package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<?> getUserStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User not logged in");
            response.put("ok", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Instant start = (startDate != null && !startDate.isEmpty() && !startDate.equals("null")) ? LocalDate.parse(startDate).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant end = (endDate != null && !endDate.isEmpty() && !endDate.equals("null")) ? LocalDate.parse(endDate).atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant() : null;

        Map<String, Object> statistics = statisticsService.getUserStatistics(userId, start, end);

        if (statistics.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "No statistics found");
            response.put("ok", true);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", statistics);
        response.put("message", "Statistics retrieved");
        response.put("ok", true);
        System.out.println("Statistics retrieved"+response);

        return ResponseEntity.ok(response);
    }
}
