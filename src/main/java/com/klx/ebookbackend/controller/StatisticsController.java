package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.service.StatisticsService;
import com.klx.ebookbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Map<String, Object> statistics = statisticsService.getPurchaseStatistics(userId, startDate, endDate);

        if (statistics == null) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        Map<String, Integer> bookQuantities = (Map<String, Integer>) statistics.get("bookQuantities");
        Map<String, Double> bookTotalPrices = (Map<String, Double>) statistics.get("bookTotalPrices");
        int totalBooks = (int) statistics.get("totalBooks");
        double totalAmount = (double) statistics.get("totalAmount");

        List<Map<String, Object>> books = bookQuantities.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> bookMap = new HashMap<>();
                    bookMap.put("title", entry.getKey());
                    bookMap.put("quantity", entry.getValue());
                    bookMap.put("totalPrice", bookTotalPrices.get(entry.getKey()));
                    return bookMap;
                })
                .collect(Collectors.toList());

        int totalItems = books.size();
        List<Map<String, Object>> paginatedBooks = (List<Map<String, Object>>) paginateList(books, pageIndex, pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("books", paginatedBooks);
        response.put("totalBooks", totalBooks);
        response.put("totalAmount", totalAmount);
        response.put("totalItems", totalItems);

        return ResponseEntity.ok(response);
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

        Map<String, Object> salesStats = statisticsService.getSalesStatistics(startDate, endDate);
        if (salesStats == null || !salesStats.containsKey("sales")) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        List<?> sales = (List<?>) salesStats.get("sales");
        int totalItems = sales.size();
        List<?> paginatedSales = paginateList(sales, pageIndex, pageSize);

        salesStats.put("sales", paginatedSales);
        salesStats.put("totalItems", totalItems);

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

        Map<String, Object> userStats = statisticsService.getUserPurchaseStatistics(startDate, endDate);
        if (userStats == null || !userStats.containsKey("users")) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        List<?> users = (List<?>) userStats.get("users");
        int totalItems = users.size();
        List<?> paginatedUsers = paginateList(users, pageIndex, pageSize);

        userStats.put("users", paginatedUsers);
        userStats.put("totalItems", totalItems);

        return ResponseEntity.ok(userStats);
    }

    private List<?> paginateList(List<?> list, int pageIndex, int pageSize) {
        return list.stream()
                .skip(pageIndex * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }
}
