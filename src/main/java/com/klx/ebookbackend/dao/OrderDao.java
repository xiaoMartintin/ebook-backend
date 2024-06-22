package com.klx.ebookbackend.dao;

import com.klx.ebookbackend.entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Order saveOrder(Order order);
    void deleteOrder(int id);
    Optional<Order> getOrderById(int id);
    Optional<Order> getOrdersByUserId(int userId);
}