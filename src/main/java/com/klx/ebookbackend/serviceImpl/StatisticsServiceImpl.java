package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.service.StatisticsService;
import com.klx.ebookbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserService userService;

    @Override
    public Map<String, Object> getPurchaseStatistics(Integer userId, LocalDate startDate, LocalDate endDate, int pageIndex, int pageSize) {
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.EPOCH;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.now();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<Order> orders = orderDao.getOrdersByUserIdAndTimeBetween(userId, startInstant, endInstant, pageable);
        if (orders.isEmpty()) {
            return new HashMap<>(); // 返回空结果避免空指针异常
        }

        Map<String, Integer> bookQuantities = new HashMap<>();
        Map<String, Double> bookTotalPrices = new HashMap<>();
        double totalAmount = 0.0;
        int totalBooks = 0;

        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                String bookTitle = item.getBook().getTitle();
                int quantity = item.getQuantity();
                double totalPrice = quantity * item.getBook().getPrice();

                bookQuantities.put(bookTitle, bookQuantities.getOrDefault(bookTitle, 0) + quantity);
                bookTotalPrices.put(bookTitle, bookTotalPrices.getOrDefault(bookTitle, 0.0) + totalPrice);

                totalAmount += totalPrice;
                totalBooks += quantity;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookQuantities", bookQuantities);
        result.put("bookTotalPrices", bookTotalPrices.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Double.parseDouble(String.format("%.2f", e.getValue())))));
        result.put("totalAmount", Double.parseDouble(String.format("%.2f", totalAmount)));
        result.put("totalBooks", totalBooks);

        return result;
    }

    @Override
    public Map<String, Object> getSalesStatistics(LocalDate startDate, LocalDate endDate, int pageIndex, int pageSize) {
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.EPOCH;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.now();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<Order> orders = orderDao.getOrdersByTimeBetween(startInstant, endInstant, pageable);
        if (orders.isEmpty()) {
            return new HashMap<>(); // 返回空结果避免空指针异常
        }

        Map<String, Integer> bookQuantities = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                String bookTitle = item.getBook().getTitle();
                int quantity = item.getQuantity();
                bookQuantities.put(bookTitle, bookQuantities.getOrDefault(bookTitle, 0) + quantity);
            }
        }

        List<Map<String, Object>> sortedBookQuantities = bookQuantities.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", entry.getKey());
                    map.put("quantity", entry.getValue());
                    return map;
                })
                .sorted((a, b) -> (int) b.get("quantity") - (int) a.get("quantity"))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("sales", sortedBookQuantities);
        result.put("total", sortedBookQuantities.size());

//        System.out.println(result+ "<- result: " + sortedBookQuantities);
        return result;
    }

    @Override
    public Map<String, Object> getUserPurchaseStatistics(LocalDate startDate, LocalDate endDate, int pageIndex, int pageSize) {
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.EPOCH;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.now();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<Order> orders = orderDao.getOrdersByTimeBetween(startInstant, endInstant, pageable);
        if (orders.isEmpty()) {
            return new HashMap<>(); // 返回空结果避免空指针异常
        }

        Map<Integer, Double> userPurchases = new HashMap<>();
        for (Order order : orders) {
            Integer userId = order.getUser().getId();
            double totalPrice = order.getOrderItems().stream().mapToDouble(item -> item.getQuantity() * item.getBook().getPrice()).sum();
            userPurchases.put(userId, userPurchases.getOrDefault(userId, 0.0) + totalPrice);
        }

        List<Map<String, Object>> sortedUserPurchases = userPurchases.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", entry.getKey());
                    map.put("totalPurchases", Double.parseDouble(String.format("%.2f", entry.getValue())));
                    map.put("username", userService.getUserById(entry.getKey()).get().getUsername());
                    return map;
                })
                .sorted((a, b) -> Double.compare((double) b.get("totalPurchases"), (double) a.get("totalPurchases")))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("users", sortedUserPurchases);
        result.put("total", sortedUserPurchases.size());

//        System.out.println(result+ "<- result: " + sortedUserPurchases);

        return result;
    }
}
