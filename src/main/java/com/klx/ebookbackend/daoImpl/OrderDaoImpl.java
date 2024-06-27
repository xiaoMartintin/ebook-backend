package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
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
    public Page<Order> findOrders(Integer userId, String keyword, Instant startInstant, Instant endInstant, Pageable pageable) {
        return orderRepository.findOrders(userId, keyword, startInstant, endInstant, pageable);
    }

    @Override
    public List<Order> getOrdersByUserIdAndTimeBetween(int userId, Instant startInstant, Instant endInstant) {
        return orderRepository.findByUserIdAndTimeBetween(userId, startInstant, endInstant);
    }

    @Override
    public List<Order> getOrdersByTimeBetween(Instant startInstant, Instant endInstant) {
        return orderRepository.findByTimeBetween(startInstant, endInstant);
    }

    @Override
    public Page<Order> findAllOrders(String keyword, Instant startInstant, Instant endInstant, Pageable pageable) {
        return orderRepository.findAllOrders(keyword, startInstant, endInstant, pageable);
    }
}
