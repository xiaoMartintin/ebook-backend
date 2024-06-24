package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.Order;

import java.time.LocalDate;
import java.util.*;


public interface OrderService {
    Order placeOrder(Order order);
    List<Order> getOrders(Integer userId, String keyword, LocalDate startDate, LocalDate endDate);
}