package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Collections;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Override
    public Order placeOrder(Order order) {
        return orderDao.saveOrder(order);
    }

    @Override
    public Optional<Order> getOrders(Integer userId) {
        return orderDao.getOrdersByUserId(userId);
    }
}
