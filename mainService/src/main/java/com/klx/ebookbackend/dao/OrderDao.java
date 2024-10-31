package com.klx.ebookbackend.dao;

import com.klx.ebookbackend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Order saveOrder(Order order);
    void deleteOrder(int id);
    Optional<Order> getOrderById(int id);
    List<Order> getOrdersByUserId(int userId);
    //    List<Order> findOrders(Integer userId, String keyword, Instant startInstant, Instant endInstant);
    List<Order> getOrdersByUserIdAndTimeBetween(int userId, Instant startInstant, Instant endInstant);
    List<Order> getOrdersByTimeBetween(Instant startInstant, Instant endInstant);
    //    List<Order> findAllOrders(String keyword, Instant startInstant, Instant endInstant);
    Page<Order> findOrders(Integer userId, String keyword, Instant startDate, Instant endDate, Pageable pageable);
    Page<Order> findAllOrders(String keyword, Instant startDate, Instant endDate, Pageable pageable);
}
