package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public Order saveOrder(Order order){
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(int id){
        orderRepository.deleteById(id);
    }

    @Override
    public Optional<Order> getOrderById(int id){
        return orderRepository.findById(id);
    }
    @Override
    public List<Order> getOrdersByUserId(int userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findOrders(Integer userId, String keyword, Instant startDate, Instant endDate) {
        return orderRepository.findOrders(userId, keyword, startDate, endDate);
    }

    @Override
    public List<Order> getOrdersByUserIdAndTimeBetween(int userId, Instant startDate, Instant endDate) {
        return orderRepository.findByUserIdAndTimeBetween(userId, startDate, endDate);
    }

}