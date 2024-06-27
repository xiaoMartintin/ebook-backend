package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order placeOrder(Order order);
    Page<Order> getOrders(Integer userId, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest);
    Page<Order> getAllOrders(String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest);
}
