package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private OrderDao orderDao;

    @Override
    public Map<String, Object> getPurchaseStatistics(Integer userId, LocalDate startDate, LocalDate endDate) {
        //因为order_item都用的精确时间戳Instant所以这里也必须转化成Instant
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.EPOCH;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.now();

        List<Order> orders = orderDao.getOrdersByUserIdAndTimeBetween(userId, startInstant, endInstant);

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
        result.put("bookTotalPrices", bookTotalPrices);
        result.put("totalAmount", totalAmount);
        result.put("totalBooks", totalBooks);

        return result;
    }
}
