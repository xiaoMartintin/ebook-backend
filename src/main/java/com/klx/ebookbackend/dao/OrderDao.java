package com.klx.ebookbackend.dao;

import com.klx.ebookbackend.entity.Order;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Order saveOrder(Order order);
    void deleteOrder(int id);
    Optional<Order> getOrderById(int id);
    List<Order> getOrdersByUserId(int userId);
    List<Order> findOrders(Integer userId, String keyword, Instant startDate, Instant endDate);
    List<Order> getOrdersByUserIdAndTimeBetween(int userId, Instant startDate, Instant endDate);
}