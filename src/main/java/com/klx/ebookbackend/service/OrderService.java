package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.entity.Order;

import java.util.*;


public interface OrderService {
    Order placeOrder(Order order);
    Optional<Order> getOrders(Integer userId);
}